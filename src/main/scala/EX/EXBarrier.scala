package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val aluResult = new InOutBundle(SInt())

            val memInputData = new InOutBundle(SInt())

            val branchAddr = new InOutBundle(UInt())
            val branchTaken = new InOutBundle(Bool())

            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    io.aluResult.out := RegNext(io.aluResult.in)

    io.memInputData.out := RegNext(io.memInputData.in, 0.S)

    io.branchAddr.out := RegNext(io.branchAddr.in, 0.U)
    io.branchTaken.out := RegNext(io.branchTaken.in, false.B)

    io.pc.out := RegNext(io.pc.in, 0.U)

    io.controlSignals <> Module(new ControlSignalBarrier()).io
}