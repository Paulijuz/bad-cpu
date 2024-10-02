package FiveStage

import chisel3._

import Latch._

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
    val io = IO(new Bundle {
        val stall = Input(Bool())
        val flush = Input(Bool())
        val controlSignals = new InOutBundle(new ControlSignalsBundle())
    })

    // TODO: Refactor

    io.controlSignals.out.aluOp := latch(io.controlSignals.in.aluOp, io.stall, io.flush)

    io.controlSignals.out.branchType := latch(io.controlSignals.in.branchType, io.stall, io.flush)
    io.controlSignals.out.branch := latch(io.controlSignals.in.branch, io.stall, io.flush)
    io.controlSignals.out.jump := latch(io.controlSignals.in.jump, io.stall, io.flush)
    
    io.controlSignals.out.regWriteAddress := latch(io.controlSignals.in.regWriteAddress, io.stall, io.flush)
    io.controlSignals.out.regWriteEnable := latch(io.controlSignals.in.regWriteEnable, io.stall, io.flush)
    
    io.controlSignals.out.memWriteEnable := latch(io.controlSignals.in.memWriteEnable, io.stall, io.flush)
    io.controlSignals.out.memReadEnable := latch(io.controlSignals.in.memReadEnable, io.stall, io.flush)
}