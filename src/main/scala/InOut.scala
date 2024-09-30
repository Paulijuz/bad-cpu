package FiveStage

import chisel3._

class InOutBundle[T<:Data](source: T) extends Bundle {
  val in = Input(source)
  val out = Output(source)
}
