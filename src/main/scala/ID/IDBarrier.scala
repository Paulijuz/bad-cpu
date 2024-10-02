package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())

            val operand1 = new InOutBundle(SInt())
            val operand2 = new InOutBundle(SInt())
                        
            val memInputData = new InOutBundle(SInt())
            
            val imm = new InOutBundle(SInt())
            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    inOutLatch(io.operand1, io.stall)
    inOutLatch(io.operand2, io.stall)
    
    inOutLatch(io.imm, io.stall)
    inOutLatch(io.pc, io.stall)
    
    inOutLatch(io.memInputData, io.stall)
    
    val controlSignalBarrier = Module(new ControlSignalBarrier())
    controlSignalBarrier.io.stall := io.stall
    io.controlSignals <> controlSignalBarrier.io.controlSignals
}
