package FiveStage

import chisel3._

object Latch {
        def latch[T <: Data](input: T, stall: Bool = false.B, flush: Bool = false.B): T = {
            val reg = RegInit(0.U.asTypeOf(input))
            
            when (!stall) { reg := input }
            when (flush) { reg := 0.U.asTypeOf(input) }

            reg
        }

    def inOutLatch[T <: Data](input: InOutBundle[T], stall: Bool = false.B, flush: Bool = false.B): Unit = {
        val reg = RegInit(0.U.asTypeOf(input.in))
        
        when (!stall) { reg := input.in }
        when (flush) { reg := 0.U.asTypeOf(input.in) }

        input.out := reg
    }
}