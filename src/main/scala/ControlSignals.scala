package FiveStage

import chisel3._

class ControlSignalsBundle extends Bundle {
    val aluOp = UInt(4.W)

    val branchType = UInt()
    val branch = Bool()
    val jump = Bool()

    val regWriteAddress = UInt()
    val regWriteEnable = Bool()

    val memWriteEnable = Bool()
    val memReadEnable = Bool()
}

class ControlSignalBarrier extends Module {
    val io = IO(new InOutBundle(new ControlSignalsBundle()))

    io.out.aluOp := RegNext(io.in.aluOp, 0.U)

    io.out.branchType := RegNext(io.in.branchType, 0.U)
    io.out.branch := RegNext(io.in.branch, false.B)
    io.out.jump := RegNext(io.in.jump, false.B)
    
    io.out.regWriteAddress := RegNext(io.in.regWriteAddress, 0.U)
    io.out.regWriteEnable := RegNext(io.in.regWriteEnable, false.B)
    
    io.out.memWriteEnable := RegNext(io.in.memWriteEnable, false.B)
    io.out.memReadEnable := RegNext(io.in.memReadEnable, false.B)
}