package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val data = new InOutBundle(SInt())
            val regWriteEnable = new InOutBundle(Bool())
            val regWriteAddress = new InOutBundle(UInt())
        }
    )

    io.regWriteEnable.out := RegNext(io.regWriteEnable.in, false.B)
    io.regWriteAddress.out := RegNext(io.regWriteAddress.in, 0.U)

    io.data.out := io.data.in
}
