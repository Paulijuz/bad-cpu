package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())
            val flush = Input(Bool())

            val rs1Data = new InOutBundle(UInt())
            val rs1Addr = new InOutBundle(UInt())
            val rs2Data = new InOutBundle(UInt())
            val rs2Addr = new InOutBundle(UInt())
                        
            val imm = new InOutBundle(SInt())
            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    inOutLatch(io.rs1Data, io.stall)
    inOutLatch(io.rs1Addr, io.stall)
    inOutLatch(io.rs2Data, io.stall)
    inOutLatch(io.rs2Addr, io.stall)
    
    inOutLatch(io.imm, io.stall)
    inOutLatch(io.pc, io.stall)
    
    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := io.stall
    controlSignalBarrier.io.flush := io.flush
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}
