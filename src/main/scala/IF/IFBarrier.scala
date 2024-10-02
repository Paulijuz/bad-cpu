package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Latch._

class IFBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val stall = Input(Bool())

            val pc = new InOutBundle(UInt())
            val instruction = new InOutBundle(new Instruction)
        }
    )

    inOutLatch(io.pc, io.stall)

    // Since the instruction is already delayed by one cycle from reading the IMEM
    // we don't have to add a once cycle delay with a latch in the barrier.
    // This also means that it's the IF stage's repsonsibility to stall the instruction
    // since the barrier doesn't have a register in which it can hold the instruction 
    // during a stall.
    io.instruction.out := io.instruction.in
}
