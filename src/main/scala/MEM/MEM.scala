package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val ALURes = Input(SInt())
      
      val writeData = Input(SInt())
      val writeEnable = Input(Bool())
      val readEnable = Input(Bool())

      val jump = Input(Bool())
      val PC = Input(UInt())

      val data = Output(SInt())
    }
  )


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates


  /**
    * Your code here.
    */
  DMEM.io.dataIn      := io.writeData.asUInt()
  DMEM.io.dataAddress := io.ALURes.asUInt()
  DMEM.io.writeEnable := io.writeEnable

  val aluRes = RegNext(io.ALURes) 
  val readEnable = RegNext(io.readEnable)
  val jump = RegNext(io.jump)
  val pc = RegNext(io.PC)

  io.data := Mux(jump, (pc + 4.U).asSInt(), Mux(!readEnable, aluRes, DMEM.io.dataOut.asSInt()))
}
