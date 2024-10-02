package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())

            val aluResult = new InOutBundle(SInt())

            val memInputData = new InOutBundle(SInt())

            val branchAddr = new InOutBundle(UInt())
            val branchTaken = new InOutBundle(Bool())

            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    inOutLatch(io.aluResult, io.stall)

    inOutLatch(io.memInputData, io.stall)

    inOutLatch(io.branchAddr, io.stall)
    inOutLatch(io.branchTaken, io.stall)

    inOutLatch(io.pc, io.stall)

    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := io.stall
    controlSignalBarrier.io.flush := false.B
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}