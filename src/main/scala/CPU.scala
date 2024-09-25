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

  IF.io.branchTaken := EXBarrier.branchTakenOut
  IF.io.branchAddr := EXBarrier.branchAddrOut

  IFBarrier.PCIn := IF.io.PC
  IFBarrier.instructionIn := IF.io.instruction

  ID.io.PC := IFBarrier.PCOut
  ID.io.instruction := IFBarrier.instructionOut

  ID.io.writeEnable := MEMBarrier.regWriteEnableOut
  ID.io.writeData := MEMBarrier.dataOut
  ID.io.writeAddress := MEMBarrier.regWriteAddressOut

  IDBarrier.operand1In := ID.io.operand1
  IDBarrier.operand2In := ID.io.operand2
  IDBarrier.ALUOpIn := ID.io.ALUOp
  IDBarrier.regWriteAddressIn := ID.io.regWriteAddress
  IDBarrier.memWriteEnableIn := ID.io.memWriteEnable
  IDBarrier.memReadEnableIn := ID.io.memReadEnable
  IDBarrier.memInputDataIn := ID.io.memInputData
  IDBarrier.regWriteEnableIn := ID.io.regWriteEnable
  IDBarrier.PCIn := IFBarrier.PCOut
  IDBarrier.immIn := ID.io.imm
  IDBarrier.branchTypeIn := ID.io.branchType
  IDBarrier.branchIn := ID.io.branch
  IDBarrier.jumpIn := ID.io.jump

  EX.io.op1 := IDBarrier.operand1Out
  EX.io.op2 := IDBarrier.operand2Out
  EX.io.aluOp := IDBarrier.ALUOpOut
  EX.io.PC := IDBarrier.PCOut
  EX.io.imm := IDBarrier.immOut
  EX.io.branchType := IDBarrier.branchTypeOut
  EX.io.branch := IDBarrier.branchOut
  EX.io.jump := IDBarrier.jumpOut

  EXBarrier.ALUResultIn := EX.io.aluResult
  EXBarrier.regWriteAddressIn := IDBarrier.regWriteAddressOut
  EXBarrier.memWriteEnableIn := IDBarrier.memWriteEnableOut
  EXBarrier.memReadEnableIn := IDBarrier.memReadEnableOut
  EXBarrier.memInputDataIn := IDBarrier.memInputDataOut
  EXBarrier.regWriteEnableIn := IDBarrier.regWriteEnableOut
  EXBarrier.branchTakenIn := EX.io.branchTaken
  EXBarrier.branchAddrIn := EX.io.branchAddr
  EXBarrier.PCIn := IDBarrier.PCOut
  EXBarrier.jumpIn := IDBarrier.jumpOut

  MEM.io.ALURes := EXBarrier.ALUResultOut
  MEM.io.writeData := EXBarrier.memInputDataOut
  MEM.io.writeEnable := EXBarrier.memWriteEnableOut
  MEM.io.readEnable := EXBarrier.memReadEnableOut
  MEM.io.PC := EXBarrier.PCOut
  MEM.io.jump := EXBarrier.jumpOut
  
  MEMBarrier.dataIn := MEM.io.data
  MEMBarrier.regWriteAddressIn := EXBarrier.regWriteAddressOut
  MEMBarrier.regWriteEnableIn := EXBarrier.regWriteEnableOut
}
