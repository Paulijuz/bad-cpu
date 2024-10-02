package FiveStage
import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule
import Latch._
import shapeless.ops.nat

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val branchAddr = Input(UInt())
      val branchTaken = Input(Bool())

      val stall = Input(Bool())

      val PC = Output(UInt())
      val instruction = Output(new Instruction)
    })

  val IMEM = Module(new IMEM)
  
  val pcReg     = RegInit(UInt(32.W), 0.U)
  val prevPcReg = RegInit(0.U)
  

  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  // Since IMEM adds a one cycle delay for the instruction we need to do some wierd
  // shenanigangs to make sure that the correct instruction is held when we stall.

  // Only update the PC when we're not stalled
  when (!io.stall) {
    pcReg := Mux(io.branchTaken, io.branchAddr, pcReg + 4.U)
    prevPcReg := pcReg // Store the previous PC so that when we stall we can use that PC instead.
  }

  val pc = Wire(UInt()) // Create a wire for PC to avoid having to rewrite the MUX bellow
  pc := Mux(io.stall, prevPcReg, pcReg) // When we stall use the previous PC.

  IMEM.io.instructionAddress := pc
  io.PC := pc

  val instruction = Wire(new Instruction)
  instruction := IMEM.io.instruction.asTypeOf(new Instruction)

  io.instruction := instruction

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    pcReg := 0.U
    instruction := Instruction.NOP
  }
}
