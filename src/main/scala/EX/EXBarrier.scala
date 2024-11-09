package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val flush = Input(Bool())

            val aluResult = new InOutBundle(SInt())

            val memWriteData = new InOutBundle(SInt())

            val correctTarget = new InOutBundle(UInt())
            val misprediction = new InOutBundle(Bool())

            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    inOutLatch(io.aluResult, false.B, io.flush)

    inOutLatch(io.memWriteData, false.B, io.flush)

    inOutLatch(io.correctTarget, false.B, io.flush)
    inOutLatch(io.misprediction, false.B, io.flush)

    inOutLatch(io.pc, false.B, io.flush)

    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := false.B
    controlSignalBarrier.io.flush := io.flush
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}