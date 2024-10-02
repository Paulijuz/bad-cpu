package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())

            val data = new InOutBundle(SInt())
            
            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    io.data.out := io.data.in

    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := io.stall
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}
