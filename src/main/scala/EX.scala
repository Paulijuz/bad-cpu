package FiveStage

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup
import ALUOps._

class Execute extends MultiIOModule {
  val io = IO(
    new Bundle {
      val aluOp = Input(UInt(4.W))
      val op1 = Input(SInt())
      val op2 = Input(SInt())

      val aluResult = Output(SInt())
    }
  )

  val ALUopMap = Array(
    ADD  -> (io.op1 + io.op2),
    SUB  -> (io.op1 - io.op2),
    OR   -> (io.op1 | io.op2),
    AND  -> (io.op1 & io.op2),
    XOR  -> (io.op1 ^ io.op2),
    SLT  -> (io.op1 < io.op2).zext(),
    SLTU -> (io.op1.asUInt() < io.op2.asUInt()).zext(),
    SRA  -> (io.op1 >> io.op2(4, 0)),
    SRL  -> (io.op1.asUInt() >> io.op2(4, 0)).asSInt(),
    SLL  -> (io.op1 << io.op2(4, 0)),
    COPY_A -> (io.op1),
    COPY_B -> (io.op2),
    // TODO: Add rest of operations
  )

  io.aluResult := MuxLookup(io.aluOp, 0x42069.S(32.W), ALUopMap)
}