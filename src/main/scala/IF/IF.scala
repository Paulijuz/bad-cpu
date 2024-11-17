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
  
  
  // Function for checking wheter an instruction is a branch (or jump) instruction or not
  def isBranchInstruction(instruction: Instruction): Bool = {
    import lookup._

    val branchInstructions = Array(BEQ, BNE, BLT, BGE, BLTU, BGEU, JAL, JALR)
    // Return true as long as there is one match (OR the entire list)
    branchInstructions.map(bitpat => instruction.asUInt === bitpat).reduce((l, r) => l || r)
  }

  val instruction = WireInit(new Instruction, Instruction.NOP)
  instruction := IMEM.io.instruction.asTypeOf(new Instruction)

  // Since the instruction memory is delayed by one cycle we have to give a NOP instruction
  // during a branch since the correct instruction won't be available immediately.
  io.instruction := Mux(io.misprediction, Instruction.NOP, instruction)
  
  val pc = Wire(UInt()) // Create a wire for PC to avoid having to rewrite the MUX bellow
  
  // Only accept target addresses from the BTB if it's a hit and the instruction is a branch instruction
  val predictionValid = isBranchInstruction(instruction) && btb.io.prediction.valid

  pc := Mux(
    io.misprediction, // If there has been a branch misprediction
    io.correctTarget, // set pc to the correct address.
    Mux(
      io.stall, // If we are stalling...
      prevPcReg, // use the previous PC so that IMEM returns the same instruction.
      Mux( // Otherwise, ...
        predictionValid, // if the BTB prediction is valid...
        btb.io.prediction.targetAddress, // use the predicted target.
        pcReg, // Otherwise, use the PC register (PC + 4).
      )
    )
  )

  // Only update the PC register and previous PC register when we're not stalling.
  when (!io.stall) {
    pcReg := pc + 4.U
    prevPcReg := pc // Store the previous PC so that when we stall we can use that PC instead.
  }

  // BTB
  btb.io.prediction.instructionAddress := pc
  btb.io.update.writeEnable            := io.misprediction
  btb.io.update.targetAddress          := io.correctTarget
  btb.io.update.instructionAddress     := io.branchAddress

  IMEM.io.instructionAddress := pc
  io.PC := pc

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    pcReg := 0.U
    instruction := Instruction.NOP
  }
}
