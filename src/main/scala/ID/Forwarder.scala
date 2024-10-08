package FiveStage
import chisel3._
import chisel3.util.MuxLookup

class Forwarder extends Module {
  val io = IO(new Bundle {
    val rs1ForwardAvailable = Output(Bool())
    val rs1ForwardData      = Output(SInt())

    val rs2ForwardAvailable = Output(Bool())
    val rs2ForwardData      = Output(SInt())

    val idRs1 = Input(UInt())
    val idRs2 = Input(UInt())

    val exRd        = Input(UInt())
    val exIsMemInst = Input(Bool())
    val exAluRes    = Input(SInt())

    val memRd        = Input(UInt())
    val memIsMemInst = Input(Bool())
    val memData      = Input(SInt())
    
    val wbRd   = Input(UInt())
    val wbData = Input(SInt())
  })

  val forwardAvailableLookup = Array(
    io.exRd  -> (!io.exIsMemInst),
    io.memRd -> (true.B),
    io.wbRd  -> (true.B),
  )

  val forwardDataLookup = Array(
    io.exRd  -> (io.exAluRes),
    io.memRd -> (io.memData),
    io.wbRd  -> (io.wbData),
  )

  io.rs1ForwardAvailable := MuxLookup(io.idRs1, false.B, forwardAvailableLookup)
  io.rs1ForwardData      := MuxLookup(io.idRs1, 0xBEEF01.S, forwardDataLookup)

  io.rs2ForwardAvailable := MuxLookup(io.idRs2, false.B, forwardAvailableLookup)
  io.rs2ForwardData      := MuxLookup(io.idRs2, 0xBEEF02.S, forwardDataLookup)
}
