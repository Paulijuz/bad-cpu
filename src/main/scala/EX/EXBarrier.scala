package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val aluResult = new InOutBundle(SInt())

            val regWriteAddress = new InOutBundle(UInt())
            val regWriteEnable = new InOutBundle(Bool())

            val memWriteEnable = new InOutBundle(Bool())
            val memInputData = new InOutBundle(SInt())
            val memReadEnable = new InOutBundle(Bool())

            val branchTaken = new InOutBundle(Bool())
            val branchAddr = new InOutBundle(UInt())

            val pc = new InOutBundle(UInt())
            val jump = new InOutBundle(Bool())
        }
    )

    io.aluResult.out := RegNext(io.aluResult.in)

    io.regWriteEnable.out := RegNext(io.regWriteEnable.in, false.B)
    io.regWriteAddress.out := RegNext(io.regWriteAddress.in, 0.U)

    io.memWriteEnable.out := RegNext(io.memWriteEnable.in, false.B)
    io.memReadEnable.out := RegNext(io.memReadEnable.in, false.B)
    io.memInputData.out := RegNext(io.memInputData.in, 0.S)

    io.branchTaken.out := RegNext(io.branchTaken.in, false.B)
    io.branchAddr.out := RegNext(io.branchAddr.in, 0.U)

    io.pc.out := RegNext(io.pc.in, 0.U)
    io.jump.out := RegNext(io.jump.in, false.B)
}