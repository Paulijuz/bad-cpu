package FiveStage
import chisel3._
import chisel3.util.MuxLookup

class Forwarder extends Module {
  val io = IO(new Bundle {
    val rs1ForwardAvailable = Output(Bool())
    val rs1ForwardData      = Output(SInt())

    val rs2ForwardAvailable = Output(Bool())
    val rs2ForwardData      = Output(SInt())

    val rs1 = Input(UInt())
    val rs2 = Input(UInt())

    val exRd   = Input(UInt())
    val exData = Input(SInt(32.W))

    val memRd   = Input(UInt())
    val memData = Input(SInt(32.W))
  })
  
  
    // We also include the write back register address delayed by one cycle
    // to compensate for the fact that updating a register takes one cycle.
  val prevMemRd    = RegNext(io.memRd)
  val prevMemData = RegNext(io.memData)

  val writeBackAddresses = Vec(io.exRd, io.memRd, prevMemRd)

  val forwardDataLookup = Array(
    io.exRd   -> (io.exData),
    io.memRd  -> (io.memData),
    prevMemRd -> (prevMemData),
  )

  io.rs1ForwardAvailable := io.rs1 != 0.U && writeBackAddresses.contains(io.rs1)
  io.rs1ForwardData      := MuxLookup(io.rs1, 0xBEEF01.S, forwardDataLookup)

  io.rs2ForwardAvailable := io.rs2 != 0.U && writeBackAddresses.contains(io.rs2)
  io.rs2ForwardData      := MuxLookup(io.rs2, 0xBEEF02.S, forwardDataLookup)
}
