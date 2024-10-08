package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val flush = Input(Bool())

            val aluResult = new InOutBundle(SInt())

            val memInputData = new InOutBundle(SInt())

            val branchAddr = new InOutBundle(UInt())
            val branchTaken = new InOutBundle(Bool())

            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    inOutLatch(io.aluResult, false.B, io.flush)

    inOutLatch(io.memInputData, false.B, io.flush)

    inOutLatch(io.branchAddr, false.B, io.flush)
    inOutLatch(io.branchTaken, false.B, io.flush)

    inOutLatch(io.pc, false.B, io.flush)

    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := false.B
    controlSignalBarrier.io.flush := io.flush
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}