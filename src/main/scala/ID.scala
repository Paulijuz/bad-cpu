package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup


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

      val operand1 = Output(SInt())
      val operand2 = Output(SInt())
      val imm = Output(SInt())

      val ALUOp = Output(UInt(4.W))
      val immType = Output(UInt())

      val regWriteAddress = Output(UInt())
      val regWriteEnable = Output(Bool())

      val writeEnable = Input(Bool())
      val writeAddress = Input(UInt())
      val writeData = Input(SInt())

      val memWriteEnable = Output(Bool())
      val memReadEnable = Output(Bool())

      val memInputData = Output(SInt())

      val branchType = Output(UInt())
      val branch = Output(Bool())
      val jump = Output(Bool())
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

  var immLookup = Array(
    ImmFormat.ITYPE -> io.instruction.immediateIType,
    ImmFormat.BTYPE -> io.instruction.immediateBType,
    ImmFormat.JTYPE -> io.instruction.immediateJType,
    ImmFormat.STYPE -> io.instruction.immediateSType,
    ImmFormat.UTYPE -> io.instruction.immediateUType,

  )

  var imm = MuxLookup(decoder.immType, 0xDEADBEEF.S, immLookup)
  
  var operand1Lookup = Array(
    Op1Select.PC -> io.PC.asSInt(),
    Op2Select.rs2 -> registers.io.readData1.asSInt(),
  )

  var operand2Lookup = Array(
    Op2Select.imm -> imm,
    Op2Select.rs2 -> registers.io.readData2.asSInt(),
  )

  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData.asUInt()

  io.operand1 := MuxLookup(decoder.op1Select, 0x69.S, operand1Lookup)
  io.operand2 := MuxLookup(decoder.op2Select, 0x42.S, operand2Lookup)
  io.imm := imm

  io.regWriteAddress := io.instruction.registerRd
  io.regWriteEnable := decoder.controlSignals.regWrite

  io.memWriteEnable := decoder.controlSignals.memWrite
  io.memReadEnable := decoder.controlSignals.memRead
  
  io.memInputData := registers.io.readData2.asSInt()

  io.ALUOp := decoder.ALUop
  io.immType := decoder.immType

  io.branchType := decoder.branchType
  io.branch := decoder.controlSignals.branch
  io.jump := decoder.controlSignals.jump

  decoder.instruction := io.instruction
}
