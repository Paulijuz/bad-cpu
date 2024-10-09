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

  ////////
  // IF //
  ////////

  IF.io.stall := ID.io.stall

  IF.io.branchTaken := EXBarrier.branchTaken.out
  IF.io.branchAddr := EXBarrier.branchAddr.out


  IFBarrier.stall := ID.io.stall
  IFBarrier.flush := EXBarrier.branchTaken.out

  IFBarrier.pc.in := IF.io.PC
  IFBarrier.instruction.in := IF.io.instruction

  ////////
  // ID //
  ////////

  ID.io.PC := IFBarrier.pc.out
  ID.io.instruction := IFBarrier.instruction.out

  ID.io.writeEnable := MEMBarrier.controlSignals.out.regWriteEnable
  ID.io.writeData := MEMBarrier.data.out
  ID.io.writeAddress := MEMBarrier.controlSignals.out.regWriteAddress

  ID.io.exRd            := IDBarrier.controlSignals.out.regWriteAddress
  ID.io.aluResWriteBack := !IDBarrier.controlSignals.out.jump && !IDBarrier.controlSignals.out.memReadEnable

  IDBarrier.stall := ID.io.stall
  IDBarrier.flush := ID.io.stall || EXBarrier.branchTaken.out

  IDBarrier.rs1Data.in := ID.io.rs1Data
  IDBarrier.rs1Addr.in := ID.io.rs1Addr
  IDBarrier.rs2Data.in := ID.io.rs2Data
  IDBarrier.rs2Addr.in := ID.io.rs2Addr
  IDBarrier.pc.in := IFBarrier.pc.out
  IDBarrier.imm.in := ID.io.imm
  
  IDBarrier.controlSignals.in <> ID.io.controlSignals

  ////////
  // EX //
  ////////

  EX.io.op1Select := IDBarrier.controlSignals.out.op1Select
  EX.io.op2Select := IDBarrier.controlSignals.out.op2Select
  EX.io.aluOp := IDBarrier.controlSignals.out.aluOp
  
  EX.io.PC := IDBarrier.pc.out
  EX.io.imm := IDBarrier.imm.out
  EX.io.rs1Addr := IDBarrier.rs1Addr.out
  EX.io.rs1Data := IDBarrier.rs1Data.out
  EX.io.rs2Addr := IDBarrier.rs2Addr.out
  EX.io.rs2Data := IDBarrier.rs2Data.out

  EX.io.branchType := IDBarrier.controlSignals.out.branchType
  EX.io.branch := IDBarrier.controlSignals.out.branch
  EX.io.jump := IDBarrier.controlSignals.out.jump

  EX.io.exRd  := EXBarrier.controlSignals.out.regWriteAddress
  EX.io.exData := EXBarrier.aluResult.out
  EX.io.memRd := MEMBarrier.controlSignals.out.regWriteAddress
  EX.io.memData := MEMBarrier.data.out
  
  EXBarrier.flush := EXBarrier.branchTaken.out
  
  EXBarrier.aluResult.in := EX.io.aluResult
  EXBarrier.memWriteData.in := EX.io.memWriteData
  EXBarrier.branchAddr.in := EX.io.branchAddr
  EXBarrier.branchTaken.in := EX.io.branchTaken
  EXBarrier.pc.in := IDBarrier.pc.out

  EXBarrier.controlSignals.in <> IDBarrier.controlSignals.out

  /////////
  // MEM //
  /////////

  MEM.io.ALURes := EXBarrier.aluResult.out
  MEM.io.writeData := EXBarrier.memWriteData.out
  MEM.io.writeEnable := EXBarrier.controlSignals.out.memWriteEnable
  MEM.io.readEnable := EXBarrier.controlSignals.out.memReadEnable
  MEM.io.PC := EXBarrier.pc.out
  MEM.io.jump := EXBarrier.controlSignals.out.jump
  

  MEMBarrier.data.in := MEM.io.data

  MEMBarrier.controlSignals.in <> EXBarrier.controlSignals.out
}
