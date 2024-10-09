package FiveStage
import chisel3._
import chisel3.util.Counter

// Hazard Detection Unit
class HDU extends Module {
  val io = IO(new Bundle {
    val idRs1 = Input(UInt())
    val idRs2 = Input(UInt())
    
    val aluResWriteBack = Input(Bool())
    val exRd  = Input(UInt())
    // val memRd = Input(UInt())
    // val wbRd  = Input(UInt())

    val stall = Output(Bool())
  })

  // val rdSignals = Vec(io.exRd, io.memRd, io.wbRd)

  // // This function return true if the the given register Rs
  // // will be written to by a instruction further down the line.
  // def isRawHazard(rs: UInt): Bool = {
  //   rs != 0.U && rdSignals.exists(rd => rs === rd)
  // }

  io.stall := (io.idRs1 === io.exRd || io.idRs2 === io.exRd) && !io.aluResWriteBack
}
