package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      /**
        * TODO: Your code here.
        */
      val PC = Input(UInt())
      val instruction = Input(new Instruction)

      val registerA = Output(SInt())
      val registerB = Output(SInt())
      val imm = Output(SInt())

      val ALUOp = Output(UInt(4.W))
      val immType = Output(UInt())

      val regWriteAddress = Output(UInt())

      val writeEnable = Input(Bool())
      val writeAddress = Input(UInt())
      val writeData = Input(SInt())
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io

  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * TODO: Your code here.
    */
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData.asUInt()

  io.registerA := registers.io.readData1.asSInt()
  io.registerB := registers.io.readData2.asSInt()
  io.regWriteAddress := io.instruction.registerRd
  io.imm := io.instruction.immediateIType

  io.ALUOp := decoder.ALUop
  io.immType := decoder.immType


  decoder.instruction := io.instruction
}
