package FiveStage
import chisel3._
import Math.pow

class Btb extends Module {
    val io = IO(new Bundle {
        val prediction = (new Bundle {
            val instructionAddress = Input(UInt(32.W))
            val targetAddress      = Output(UInt(32.W))
            val valid              = Output(Bool())
        })

        val update = (new Bundle {
            val instructionAddress = Input(UInt(32.W))
            val targetAddress      = Input(UInt(32.W))
            val writeEnable        = Input(Bool())
        })
    })

    val indexBits  = 12
    val bufferSize = pow(2, indexBits).toInt

    val tagsBuffer    = SyncReadMem(bufferSize, UInt((32-indexBits).W))
    val targetBuffer  = SyncReadMem(bufferSize, UInt(32.W))
  
    val tag   = io.prediction.instructionAddress >> indexBits
    val index = io.prediction.instructionAddress % bufferSize.U

    val tagsMatch              = tagsBuffer(index) === RegNext(tag)
    val predictedTargetAddress = targetBuffer(index)
    
    io.prediction.valid         := tagsMatch
    io.prediction.targetAddress := predictedTargetAddress

    when(io.update.writeEnable) {
        val updateTag   = io.update.instructionAddress >> indexBits
        val updateIndex = io.update.instructionAddress % bufferSize.U

        tagsBuffer(updateIndex)   := updateTag
        targetBuffer(updateIndex) := io.update.targetAddress
    }
}
