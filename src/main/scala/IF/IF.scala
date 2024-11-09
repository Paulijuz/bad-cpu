package FiveStage
import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

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
      val misprediction = Input(Bool())
      val correctTarget = Input(UInt(32.W))
      val branchAddress = Input(UInt())

      val stall = Input(Bool())

      val PC = Output(UInt())
      val instruction = Output(new Instruction)
    })

  val IMEM = Module(new IMEM)
  val btb  = Module(new Btb)
  
  val pcReg     = RegInit(UInt(32.W), 0.U)
  val prevPcReg = RegInit(0.U)
  

  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress
  
  
  def isBranchInstruction(instruction: Instruction): Bool = {
    import lookup._

    val branchInstructions = Array(BEQ, BNE, BLT, BGE, BLTU, BGEU)
    branchInstructions.map(bitpat => instruction.asUInt === bitpat).reduce((l, r) => l || r)
  }

  val instruction = WireInit(new Instruction, Instruction.NOP)
  instruction := IMEM.io.instruction.asTypeOf(new Instruction)

  // Since the instruction memory is delayed by one cycle and it'll take one cycle to update the PC register after a branch
  // we'll have to give out NOP for the next two cycles after a branch.
  io.instruction := Mux(io.misprediction, Instruction.NOP, instruction)
  
  val predictionValid = isBranchInstruction(instruction) && btb.io.prediction.valid
  val pc = Wire(UInt()) // Create a wire for PC to avoid having to rewrite the MUX bellow
  pc := Mux(
    io.misprediction,
    io.correctTarget,
    Mux(
      io.stall, 
      prevPcReg,
      Mux(
        predictionValid, 
        btb.io.prediction.targetAddress, 
        pcReg,
      )
    ) // When we stall use the previous PC.
  )

  // Since IMEM adds a one cycle delay for the instruction we need to do some wierd
  // shenanigangs to make sure that the correct instruction is held when we stall.
  //
  // Only update the PC when we're not stalled.
  // Except also when a branch is mispredicted so we update the branch.
  when (!io.stall || io.misprediction) {
    pcReg := pc + 4.U
    prevPcReg := pcReg // Store the previous PC so that when we stall we can use that PC instead.
  }

  IMEM.io.instructionAddress := pc
  io.PC := pc

  // BTB
  btb.io.prediction.instructionAddress := prevPcReg
  btb.io.update.writeEnable := io.misprediction
  btb.io.update.targetAddress := io.correctTarget
  btb.io.update.instructionAddress := io.branchAddress

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    pcReg := 0.U
    instruction := Instruction.NOP
  }
}
