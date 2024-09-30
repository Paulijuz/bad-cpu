package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val dataIn = Input(SInt())
            val regWriteEnableIn = Input(Bool())
            val regWriteAddressIn = Input(UInt())

            val dataOut = Output(SInt())
            val regWriteEnableOut = Output(Bool())
            val regWriteAddressOut = Output(UInt())
        }
    )

    val regWriteAddressRegister = RegInit(0.U)
    val regWriteEnableRegister = RegInit(false.B)

    regWriteAddressRegister := io.regWriteAddressIn
    regWriteEnableRegister := io.regWriteEnableIn

    io.dataOut := io.dataIn
    io.regWriteEnableOut := regWriteEnableRegister
    io.regWriteAddressOut := regWriteAddressRegister
}
