package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val registerAIn = Input(SInt())
            val registerBIn = Input(SInt())
            val immIn = Input(SInt())
            val regWriteAddressIn = Input(UInt())

            val ALUOpIn = Input(UInt(4.W))

            val registerAOut = Output(SInt())
            val registerBOut = Output(SInt())
            val immOut = Output(SInt())
            val regWriteAddressOut = Output(UInt())

            val ALUOpOut = Output(UInt(4.W))
        }
    )

    val registerARegister = RegInit(0.S)
    val registerBRegister = RegInit(0.S)
    val immRegister = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)
    val ALUOpRegister = RegInit(0.U)

    registerARegister := io.registerAIn
    registerBRegister := io.registerBIn
    immRegister := io.immIn
    regWriteAddressRegister := io.regWriteAddressIn
    ALUOpRegister := io.ALUOpIn

    io.registerAOut := registerARegister
    io.registerBOut := registerBRegister
    io.immOut := immRegister
    io.regWriteAddressOut := regWriteAddressRegister
    io.ALUOpOut := ALUOpRegister
}
