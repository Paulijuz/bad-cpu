package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val operand1 = new InOutBundle(SInt())
            val operand2 = new InOutBundle(SInt())
                        
            val memInputData = new InOutBundle(SInt())
            
            val imm = new InOutBundle(SInt())
            val pc = new InOutBundle(UInt())

            val controlSignals = new InOutBundle(new ControlSignalsBundle())
        }
    )

    io.operand1.out := RegNext(io.operand1.in, 0.S)
    io.operand2.out := RegNext(io.operand2.in, 0.S)
    
    io.imm.out := RegNext(io.imm.in, 0.S)
    io.pc.out := RegNext(io.pc.in, 0.U)
    
    io.memInputData.out := RegNext(io.memInputData.in, 0.S)

    io.controlSignals <> Module(new ControlSignalBarrier()).io
}
