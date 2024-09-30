package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._
import matryoshka.data.id


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFBarrier  = Module(new IFBarrier).io
  val IDBarrier  = Module(new IDBarrier).io
  val EXBarrier  = Module(new EXBarrier).io
  val MEMBarrier = Module(new MEMBarrier).io

  val ID  = Module(new InstructionDecode)
  val IF  = Module(new InstructionFetch)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?) 


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */

  IF.io.branchTaken := EXBarrier.branchTaken.out
  IF.io.branchAddr := EXBarrier.branchAddr.out

  IFBarrier.pc.in := IF.io.PC
  IFBarrier.instruction.in := IF.io.instruction

  ID.io.PC := IFBarrier.pc.out
  ID.io.instruction := IFBarrier.instruction.out

  ID.io.writeEnable := MEMBarrier.regWriteEnable.out
  ID.io.writeData := MEMBarrier.data.out
  ID.io.writeAddress := MEMBarrier.regWriteAddress.out

  IDBarrier.operand1.in := ID.io.operand1
  IDBarrier.operand2.in := ID.io.operand2
  IDBarrier.ALUOp.in := ID.io.ALUOp
  IDBarrier.regWriteAddress.in := ID.io.regWriteAddress
  IDBarrier.memWriteEnable.in := ID.io.memWriteEnable
  IDBarrier.memReadEnable.in := ID.io.memReadEnable
  IDBarrier.memInputData.in := ID.io.memInputData
  IDBarrier.regWriteEnable.in := ID.io.regWriteEnable
  IDBarrier.pc.in := IFBarrier.pc.out
  IDBarrier.imm.in := ID.io.imm
  IDBarrier.branchType.in := ID.io.branchType
  IDBarrier.branch.in := ID.io.branch
  IDBarrier.jump.in := ID.io.jump

  EX.io.op1 := IDBarrier.operand1.out
  EX.io.op2 := IDBarrier.operand2.out
  EX.io.aluOp := IDBarrier.ALUOp.out
  EX.io.PC := IDBarrier.pc.out
  EX.io.imm := IDBarrier.imm.out
  EX.io.branchType := IDBarrier.branchType.out
  EX.io.branch := IDBarrier.branch.out
  EX.io.jump := IDBarrier.jump.out

  EXBarrier.aluResult.in := EX.io.aluResult
  EXBarrier.regWriteAddress.in := IDBarrier.regWriteAddress.out
  EXBarrier.memWriteEnable.in := IDBarrier.memWriteEnable.out
  EXBarrier.memReadEnable.in := IDBarrier.memReadEnable.out
  EXBarrier.memInputData.in := IDBarrier.memInputData.out
  EXBarrier.regWriteEnable.in := IDBarrier.regWriteEnable.out
  EXBarrier.branchTaken.in := EX.io.branchTaken
  EXBarrier.branchAddr.in := EX.io.branchAddr
  EXBarrier.pc.in := IDBarrier.pc.out
  EXBarrier.jump.in := IDBarrier.jump.out

  MEM.io.ALURes := EXBarrier.aluResult.out
  MEM.io.writeData := EXBarrier.memInputData.out
  MEM.io.writeEnable := EXBarrier.memWriteEnable.out
  MEM.io.readEnable := EXBarrier.memReadEnable.out
  MEM.io.PC := EXBarrier.pc.out
  MEM.io.jump := EXBarrier.jump.out
  
  MEMBarrier.data.in := MEM.io.data
  MEMBarrier.regWriteAddress.in := EXBarrier.regWriteAddress.out
  MEMBarrier.regWriteEnable.in := EXBarrier.regWriteEnable.out
}
