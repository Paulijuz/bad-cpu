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

  // The only time a stall is required is when there is a RAW hazard between
  // the next instruction and the previous instruction, where the previous instruciton
  // is either a memory instruction or a jump instruciton. This is because those two
  // instruction types won't have their wb data available in the EX stage, thus we have to
  // wait for them to eneter the mem stage before we can continue.
  io.stall := (io.idRs1 === io.exRd || io.idRs2 === io.exRd) && !io.aluResWriteBack
}
