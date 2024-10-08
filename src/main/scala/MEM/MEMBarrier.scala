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

    // Data takes one clockcycle to read so 
    io.data.out := io.data.in

    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := false.B
    controlSignalBarrier.io.flush := false.B
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}
