package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

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
        }
    )

    val ALUResultRegister = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)
    val regWriteEnableRegister = RegInit(false.B)

    val memWriteEnableOutRegister = RegInit(false.B)
    val memReadEnableOutRegister = RegInit(false.B)
    val memInputDataOutRegister = RegInit(0.S)

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
}