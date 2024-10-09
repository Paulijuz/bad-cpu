package FiveStage

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup
import ALUOps._
import javax.net.ssl.SNIHostName

class Execute extends MultiIOModule {
  val io = IO(
    new Bundle {
      val op1Select = Input(UInt())
      val op2Select = Input(UInt())
      val aluOp = Input(UInt(4.W))

      val PC = Input(UInt())
      val imm = Input(SInt())

      val rs1Addr = Input(UInt())
      val rs2Addr = Input(UInt())
      val rs1Data = Input(UInt())
      val rs2Data = Input(UInt())


      val exData = Input(SInt())
      val exRd = Input(UInt())
      val memData = Input(SInt())
      val memRd = Input(UInt())

      val branchType = Input(UInt())
      val branch = Input(Bool())
      val jump = Input(Bool())

      val aluResult = Output(SInt())
      val branchTaken = Output(Bool())
      val branchAddr = Output(UInt())

      val memWriteData = Output(SInt())
    }
  )

  val brancher = Module(new Brancher()).io
  val forwarder = Module(new Forwarder()).io

  val rs1 = Mux(forwarder.rs1ForwardAvailable, forwarder.rs1ForwardData, io.rs1Data.asSInt())
  val rs2 = Mux(forwarder.rs2ForwardAvailable, forwarder.rs2ForwardData, io.rs2Data.asSInt())
  
  var operand1Lookup = Array(
    Op1Select.PC -> io.PC.asSInt(),
    Op1Select.rs1 -> rs1,
  )

  var operand2Lookup = Array(
    Op2Select.imm -> io.imm,
    Op2Select.rs2 -> rs2,
  )

  val op1 = MuxLookup(io.op1Select, 0x69.S, operand1Lookup)
  val op2 = MuxLookup(io.op2Select, 0x42.S, operand2Lookup)

  val aluOpMap = Array(
    ADD  -> (op1 + op2),
    SUB  -> (op1 - op2),
    OR   -> (op1 | op2),
    AND  -> (op1 & op2),
    XOR  -> (op1 ^ op2),
    SLT  -> (op1 < op2).zext(),
    SLTU -> (op1.asUInt() < op2.asUInt()).zext(),
    SRA  -> (op1 >> op2(4, 0)),
    SRL  -> (op1.asUInt() >> op2(4, 0)).asSInt(),
    SLL  -> (op1 << op2(4, 0)),
    COPY_A -> (op1),
    COPY_B -> (op2),
  )
  
  io.aluResult := MuxLookup(io.aluOp, 0x42069.S(32.W), aluOpMap)
  io.memWriteData := rs2
  io.branchTaken := io.jump || io.branch && brancher.branchTaken

  brancher.branchType := io.branchType
  brancher.negative := io.aluResult < 0.S
  brancher.zero := io.aluResult === 0.S
  io.branchAddr := Mux(io.jump, io.aluResult.asUInt(), (io.imm + io.PC.asSInt()).asUInt())

  forwarder.rs1 := io.rs1Addr
  forwarder.rs2 := io.rs2Addr
  
  forwarder.exRd := io.exRd
  forwarder.exData := io.exData

  forwarder.memRd := io.memRd
  forwarder.memData := io.memData
}