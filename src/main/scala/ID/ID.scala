package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup
import com.ibm.icu.text.LocaleDisplayNames.UiListItem


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
      val PC = Input(UInt())
      val instruction = Input(new Instruction)

      val writeEnable  = Input(Bool())
      val writeAddress = Input(UInt())
      val writeData    = Input(SInt())

      val exJumpInst = Input(Bool())
      val exMemInst  = Input(Bool())
      val exRd       = Input(UInt())

      val rs1Data = Output(UInt())
      val rs1Addr = Output(UInt())
      val rs2Data = Output(UInt())
      val rs2Addr = Output(UInt())

      val imm = Output(SInt())

      val controlSignals = Output(new ControlSignalsBundle())
      
      val stall = Output(Bool())
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io
  val hdu       = Module(new HDU()).io

  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * My code
    */

  var immLookup = Array(
    ImmFormat.ITYPE -> io.instruction.immediateIType,
    ImmFormat.BTYPE -> io.instruction.immediateBType,
    ImmFormat.JTYPE -> io.instruction.immediateJType,
    ImmFormat.STYPE -> io.instruction.immediateSType,
    ImmFormat.UTYPE -> io.instruction.immediateUType,

  )

  var imm = MuxLookup(decoder.immType, 0xDEADBEEF.S, immLookup)

  // Set rs1 or rs2 to 0 when a register is not used as an operand.
  val rs1Address = Mux(
    decoder.op1Select === Op1Select.rs1, 
    io.instruction.registerRs1, 
    0.U
  )
  val rs2Address = Mux(
    decoder.op2Select === Op2Select.rs2 || decoder.controlSignals.memWrite,
    io.instruction.registerRs2,
    0.U
  )

  decoder.instruction := io.instruction
  
  registers.io.readAddress1 := rs1Address
  registers.io.readAddress2 := rs2Address
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData.asUInt()

  io.imm := imm
  io.rs1Data := registers.io.readData1
  io.rs1Addr := rs1Address
  io.rs2Data := registers.io.readData2
  io.rs2Addr := rs2Address

  // We have to avoid passing a random Rd when not writing to a register to avoid confusing the HDU
  io.controlSignals.regWriteAddress := Mux(decoder.controlSignals.regWrite, io.instruction.registerRd, 0.U)
  io.controlSignals.regWriteEnable := decoder.controlSignals.regWrite

  io.controlSignals.memWriteEnable := decoder.controlSignals.memWrite
  io.controlSignals.memReadEnable := decoder.controlSignals.memRead
  
  io.controlSignals.op1Select := decoder.op1Select
  io.controlSignals.op2Select := decoder.op2Select
  io.controlSignals.aluOp := decoder.ALUop

  io.controlSignals.branchType := decoder.branchType
  io.controlSignals.branch := decoder.controlSignals.branch
  io.controlSignals.jump := decoder.controlSignals.jump

  hdu.idRs1 := rs1Address
  hdu.idRs2 := rs2Address

  hdu.exJumpInst := io.exJumpInst
  hdu.exMemInst := io.exMemInst
  hdu.exRd  := io.exRd

  io.stall := hdu.stall
}
