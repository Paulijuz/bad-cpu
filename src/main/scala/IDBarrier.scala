package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {
    val io = IO(
        new Bundle {
            val operand1In = Input(SInt())
            val operand2In = Input(SInt())
            val regWriteAddressIn = Input(UInt())
            val regWriteEnableIn = Input(Bool())

            val ALUOpIn = Input(UInt(4.W))

            val operand1Out = Output(SInt())
            val operand2Out = Output(SInt())
            val regWriteAddressOut = Output(UInt())
            val regWriteEnableOut = Output(Bool())

            val ALUOpOut = Output(UInt(4.W))


            val memWriteEnableIn = Input(Bool())
            val memReadEnableIn = Input(Bool())

            val memInputDataIn = Input(SInt())

            val memWriteEnableOut = Output(Bool())
            val memReadEnableOut = Output(Bool())

            val memInputDataOut = Output(SInt())

            val immIn = Input(SInt())
            val immOut = Output(SInt())
            val PCIn = Input(UInt())
            val PCOut = Output(UInt())
            val branchTypeIn = Input(UInt())
            val branchTypeOut = Output(UInt())
            val branchIn = Input(Bool())
            val branchOut = Output(Bool())
            val jumpIn = Input(Bool())
            val jumpOut = Output(Bool())
        }
    )

    val operand1Register = RegInit(0.S)
    val operand2Register = RegInit(0.S)
    val regWriteAddressRegister = RegInit(0.U)
    val ALUOpRegister = RegInit(0.U)
    val regWriteEnableRegister = RegInit(false.B)

    val memWriteEnableOutRegister = RegInit(false.B)
    val memReadEnableOutRegister = RegInit(false.B)
    val memInputDataOutRegister = RegInit(0.S)

    val immRegister = RegInit(0.S)
    val PCRegister = RegInit(0.U)
    val branchTypeRegister = RegInit(0.U)
    val branchRegister = RegInit(false.B)
    val jumpRegister = RegInit(false.B)

    operand1Register := io.operand1In
    operand2Register := io.operand2In
    regWriteAddressRegister := io.regWriteAddressIn
    ALUOpRegister := io.ALUOpIn
    regWriteEnableRegister := io.regWriteEnableIn

    io.operand1Out := operand1Register
    io.operand2Out := operand2Register
    io.regWriteAddressOut := regWriteAddressRegister
    io.ALUOpOut := ALUOpRegister
    io.regWriteEnableOut := regWriteEnableRegister

    memWriteEnableOutRegister := io.memWriteEnableIn
    memReadEnableOutRegister := io.memReadEnableIn
    memInputDataOutRegister := io.memInputDataIn

    io.memWriteEnableOut := memWriteEnableOutRegister
    io.memReadEnableOut := memReadEnableOutRegister
    io.memInputDataOut := memInputDataOutRegister

    immRegister := io.immIn
    io.immOut := immRegister
    PCRegister := io.PCIn
    io.PCOut := PCRegister
    branchTypeRegister := io.branchTypeIn
    io.branchTypeOut := branchTypeRegister
    branchRegister := io.branchIn
    io.branchOut := branchRegister
    jumpRegister := io.jumpIn
    io.jumpOut := jumpRegister
}
