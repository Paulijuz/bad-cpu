package FiveStage

import chisel3._
import chisel3.experimental.MultiIOModule

class Latch[T<:Data, K<:Data](source: T, init: K) extends MultiIOModule {
  val io = IO(new Bundle {
    val in = Input(source)

    val out = Output(source)
  })

  val register = RegInit(init)

  register := io.in
  io.out := register
}
