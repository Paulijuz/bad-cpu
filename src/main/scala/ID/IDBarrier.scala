package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val operand1 = new InOutBundle(SInt())
            val operand2 = new InOutBundle(SInt())
            val regWriteAddress = new InOutBundle(UInt())
            val regWriteEnable = new InOutBundle(Bool())

            val ALUOp = new InOutBundle(UInt(4.W))

            val memWriteEnable = new InOutBundle(Bool())
            val memReadEnable = new InOutBundle(Bool())

            val memInputData = new InOutBundle(SInt())

            val imm = new InOutBundle(SInt())
            val pc = new InOutBundle(UInt())
            val branchType = new InOutBundle(UInt())
            val branch = new InOutBundle(Bool())
            val jump = new InOutBundle(Bool())
        }
    )

    io.operand1.out := RegNext(io.operand1.in, 0.S)
    io.operand2.out := RegNext(io.operand2.in, 0.S)
    io.regWriteAddress.out := RegNext(io.regWriteAddress.in, 0.U)
    io.ALUOp.out := RegNext(io.ALUOp.in, 0.U)
    io.regWriteEnable.out := RegNext(io.regWriteEnable.in, false.B)

    io.memWriteEnable.out := RegNext(io.memWriteEnable.in, false.B)
    io.memReadEnable.out := RegNext(io.memReadEnable.in, false.B)
    io.memInputData.out := RegNext(io.memInputData.in, 0.S)

    io.imm.out := RegNext(io.imm.in, 0.S)
    io.pc.out := RegNext(io.pc.in, 0.U)
    io.branchType.out := RegNext(io.branchType.in, 0.U)
    io.branch.out := RegNext(io.branch.in, false.B)
    io.jump.out := RegNext(io.jump.in, false.B)
}
