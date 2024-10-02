package FiveStage
import chisel3._
import chisel3.util.Counter

// Hazard Detection Unit
class HDU extends Module {
  val io = IO(new Bundle {
    val idRs1 = Input(UInt())
    val idRs2 = Input(UInt())

    val idRd  = Input(UInt())
    val exRd  = Input(UInt())
    val memRd = Input(UInt())

    val stall = Output(Bool())
  })

  val rdSignals = Vec(io.idRd, io.exRd, io.memRd)

  // This function return true if the the given register Rs
  // will be written to by a instruction further down the line.
  def isRawHazard(rs: UInt): Bool = {
    rs != 0.U && rdSignals.exists(rd => rs === rd)
  }

  // If either Rs1 or Rs2 is a RAW hazard stall until it's no longer a problem.
  io.stall := isRawHazard(io.idRs1) || isRawHazard(io.idRs2)
}
