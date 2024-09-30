package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IFBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val pc = new InOutBundle(UInt())
            val instruction = new InOutBundle(new Instruction)
        }
    )

    io.pc.out := RegNext(io.pc.in, 0.U)

    io.instruction.out := io.instruction.in
}
