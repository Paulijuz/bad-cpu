package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class IFBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())

            val pc = new InOutBundle(UInt())
            val instruction = new InOutBundle(new Instruction)
        }
    )

    inOutLatch(io.pc, io.stall)

    io.instruction.out := io.instruction.in
}
