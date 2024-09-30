package FiveStage
import chisel3._
import chisel3.util.Lookup
import chisel3.util.MuxLookup

class Brancher() extends Module {
    val io = IO(new Bundle {
        val zero = Input(Bool())
        val negative = Input(Bool())
        val branchType = Input(UInt())

        val branchTaken = Output(Bool())
    })

    val branchLookup = Array(
        branchType.beq  -> io.zero,
        branchType.gte  -> io.zero,
        branchType.gteu -> io.zero,
        branchType.lt   -> !io.zero,
        branchType.ltu  -> !io.zero,
        branchType.neq  -> !io.zero,
        branchType.jump -> true.B,
    )

    io.branchTaken := MuxLookup(io.branchType, false.B, branchLookup)
}