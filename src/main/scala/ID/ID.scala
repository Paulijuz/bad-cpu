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

      val exRd  = Input(UInt())
      val memRd = Input(UInt())
      val wbRd  = Input(UInt())

      val operand1 = Output(SInt())
      val operand2 = Output(SInt())

      val imm = Output(SInt())

      val memInputData = Output(SInt())

      val controlSignals = Output(new ControlSignalsBundle())
      
      val stall = Output(Bool())
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io
  val hdu       = Module(new HDU()).io
  val forwarder = Module(new Forwarder()).io

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
  
  var operand1Lookup = Array(
    Op1Select.PC -> io.PC.asSInt(),
    Op2Select.rs2 -> Mux(hdu.rs1Raw, forwarder.rs1ForwardData, registers.io.readData1.asSInt()),
  )

  var operand2Lookup = Array(
    Op2Select.imm -> imm,
    Op2Select.rs2 -> Mux(hdu.rs2Raw, forwarder.rs2ForwardData, registers.io.readData2.asSInt()),
  )

  decoder.instruction := io.instruction
  
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData.asUInt()

  io.operand1 := MuxLookup(decoder.op1Select, 0x69.S, operand1Lookup)
  io.operand2 := MuxLookup(decoder.op2Select, 0x42.S, operand2Lookup)
  io.imm := imm

  io.memInputData := registers.io.readData2.asSInt()

  // We have to avoid passing a random Rd when not writing to a register to avoid confusing the HDU
  io.controlSignals.regWriteAddress := Mux(decoder.controlSignals.regWrite, io.instruction.registerRd, 0.U)
  io.controlSignals.regWriteEnable := decoder.controlSignals.regWrite

  io.controlSignals.memWriteEnable := decoder.controlSignals.memWrite
  io.controlSignals.memReadEnable := decoder.controlSignals.memRead
  
  io.controlSignals.aluOp := decoder.ALUop

  io.controlSignals.branchType := decoder.branchType
  io.controlSignals.branch := decoder.controlSignals.branch
  io.controlSignals.jump := decoder.controlSignals.jump

  // Set rs1 or rs2 to 0 when a register is not used as an operand.
  val rs1 = Mux(decoder.op1Select === Op1Select.rs1, io.instruction.registerRs1, 0.U)
  val rs2 = Mux(decoder.op2Select === Op2Select.rs2 || decoder.controlSignals.memWrite, io.instruction.registerRs2, 0.U)

  hdu.idRs1 := rs1
  hdu.idRs2 := rs2

  hdu.exRd  := io.exRd
  hdu.memRd := io.memRd
  hdu.wbRd  := io.wbRd

  forwarder.idRs1 := rs1
  forwarder.idRs2 := rs2

  forwarder.exRd  := io.exRd
  forwarder.exIsMemInst := false.B
  forwarder.memRd := io.memRd
  forwarder.memIsMemInst := false.B
  forwarder.wbRd  := io.wbRd

  forwarder.exAluRes := 0.S
  forwarder.memData := 0.S
  forwarder.wbData := 0.S

  io.stall := hdu.rs1Raw /*&& !forwarder.rs1ForwardAvailable*/ || hdu.rs2Raw /*&& !forwarder.rs2ForwardAvailable*/
}
