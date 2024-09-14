package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


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

  IFBarrier.PCIn := IF.io.PC
  IFBarrier.instructionIn := IF.io.instruction

  ID.io.PC := IFBarrier.PCOut
  ID.io.instruction := IFBarrier.instructionOut

  ID.io.writeEnable := MEMBarrier.regWriteEnableOut
  ID.io.writeData := MEMBarrier.readDataOut
  ID.io.writeAddress := MEMBarrier.regWriteAddressOut

  IDBarrier.registerAIn := ID.io.registerA
  IDBarrier.registerBIn := ID.io.registerB
  IDBarrier.ALUOpIn := ID.io.ALUOp
  IDBarrier.regWriteAddressIn := ID.io.regWriteAddress
  IDBarrier.immIn := ID.io.imm

  // EX.io.op1 := IDBarrier.registerAOut
  EX.io.op2 := IDBarrier.registerAOut
  EX.io.op1 := IDBarrier.immOut
  EX.io.aluOp := IDBarrier.ALUOpOut

  EXBarrier.ALUResultIn := EX.io.aluResult
  EXBarrier.regWriteAddressIn := IDBarrier.regWriteAddressOut

  MEM.io.writeData := EXBarrier.ALUResultOut
  
  MEMBarrier.readDataIn := MEM.io.readData
  MEMBarrier.regWriteAddressIn := EXBarrier.regWriteAddressOut
}
