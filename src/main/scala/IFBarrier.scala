package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IFBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val PCIn = Input(UInt())
            val instructionIn = Input(new Instruction)

            val PCOut = Output(UInt())
            val instructionOut = Output(new Instruction)
        }
    )

    val PCReg = RegInit(0.U(32.W))

    PCReg := io.PCIn
    io.PCOut := PCReg

    io.instructionOut := io.instructionIn
}
