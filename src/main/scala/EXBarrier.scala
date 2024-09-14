package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class EXBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val ALUResultIn = Input(SInt())
            val regWriteAddressIn = Input(UInt())

            val ALUResultOut = Output(SInt())
            val regWriteAddressOut = Output(UInt())
        }
    )

    val ALUResultRegister = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)

    ALUResultRegister := io.ALUResultIn
    regWriteAddressRegister := io.regWriteAddressIn

    io.ALUResultOut := ALUResultRegister
    io.regWriteAddressOut := regWriteAddressRegister
}