package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val readDataIn = Input(SInt())
            val regWriteAddressIn = Input(UInt())

            val readDataOut = Output(SInt())
            val regWriteEnableOut = Output(Bool())
            val regWriteAddressOut = Output(UInt())
        }
    )

    val readDataRegister = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)

    readDataRegister := io.readDataIn
    regWriteAddressRegister := io.regWriteAddressIn

    io.readDataOut := readDataRegister
    io.regWriteEnableOut := true.B
    io.regWriteAddressOut := regWriteAddressRegister
}
