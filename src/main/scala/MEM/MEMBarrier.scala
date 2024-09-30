package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val data = new InOutBundle(SInt())
            
            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    io.data.out := io.data.in

    io.controlSignals <> Module(new ControlSignalBarrier()).io
}
