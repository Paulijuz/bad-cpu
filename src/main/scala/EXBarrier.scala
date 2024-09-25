package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import Chisel.OUTPUT

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val ALUResultIn = Input(SInt())
            val regWriteAddressIn = Input(UInt())
            val regWriteEnableIn = Input(Bool())

            val ALUResultOut = Output(SInt())
            val regWriteAddressOut = Output(UInt())
            val regWriteEnableOut = Output(Bool())

            val memWriteEnableIn = Input(Bool())
            val memReadEnableIn = Input(Bool())

            val memInputDataIn = Input(SInt())

            val memWriteEnableOut = Output(Bool())
            val memReadEnableOut = Output(Bool())

            val memInputDataOut = Output(SInt())

            val branchTakenIn = Input(Bool())
            val branchTakenOut = Output(Bool())

            val branchAddrIn = Input(UInt())
            val branchAddrOut = Output(UInt())

            val PCIn = Input(UInt())
            val PCOut = Output(UInt())
            val jumpIn = Input(Bool())
            val jumpOut = Output(Bool())
        }
    )

    val ALUResultRegister = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)
    val regWriteEnableRegister = RegInit(false.B)

    val memWriteEnableOutRegister = RegInit(false.B)
    val memReadEnableOutRegister = RegInit(false.B)
    val memInputDataOutRegister = RegInit(0.S)

    val branchTakenRegister = RegInit(false.B)
    val branchAddrRegister = RegInit(0.U)

    val PCRegister = RegInit(0.U)
    val jumpRegister = RegInit(false.B)

    ALUResultRegister := io.ALUResultIn
    regWriteAddressRegister := io.regWriteAddressIn

    io.ALUResultOut := ALUResultRegister
    io.regWriteAddressOut := regWriteAddressRegister

    memWriteEnableOutRegister := io.memWriteEnableIn
    memReadEnableOutRegister := io.memReadEnableIn
    memInputDataOutRegister := io.memInputDataIn
    regWriteEnableRegister := io.regWriteEnableIn

    io.memWriteEnableOut := memWriteEnableOutRegister
    io.memReadEnableOut := memReadEnableOutRegister
    io.memInputDataOut := memInputDataOutRegister
    io.regWriteEnableOut := regWriteEnableRegister

    branchTakenRegister := io.branchTakenIn
    io.branchTakenOut := branchTakenRegister
    branchAddrRegister := io.branchAddrIn
    io.branchAddrOut := branchAddrRegister

    PCRegister := io.PCIn
    io.PCOut := PCRegister
    jumpRegister := io.jumpIn
    io.jumpOut := jumpRegister
}