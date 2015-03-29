package ChiselScratch.USRP.chisel

import Chisel._

class AddSubIO(dataWidth: Int = 16) extends Bundle {
  val i0 = new DecoupledIO(Bits(width=dataWidth*2)).flip
  val i1 = new DecoupledIO(Bits(width=dataWidth*2)).flip
  val sum = new DecoupledIO(Bits(width=dataWidth*2))
  val diff = new DecoupledIO(Bits(width=dataWidth*2))
}

class AddSub(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)

  io.sum.bits := PackedSIMD(io.i0.bits,io.i1.bits,dataWidth,2,(_+_))
  io.diff.bits := PackedSIMD(io.i0.bits,io.i1.bits,dataWidth,2,(_-_))

  val ins = GroupDecoupled(io.i0,io.i1)
  val outs = GroupDecoupled(io.sum,io.diff)
  SyncDecoupled(ins,outs)
}
