package ChiselScratch.USRP.chisel

import Chisel._

class AddSubIO(dataWidth: Int = 16) extends Bundle {
  val i0 = new DecoupledIO(new StreamPayload(2*dataWidth)).flip
  val i1 = new DecoupledIO(new StreamPayload(2*dataWidth)).flip
  val sum = new DecoupledIO(new StreamPayload(2*dataWidth))
  val diff = new DecoupledIO(new StreamPayload(2*dataWidth))
}

class AddSub(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)

  io.sum.bits.last := io.i0.bits.last
  io.diff.bits.last := io.i0.bits.last

  io.sum.bits.data := PackedSIMD(io.i0.bits.data,io.i1.bits.data,dataWidth,2,(_+_))
  io.diff.bits.data := PackedSIMD(io.i0.bits.data,io.i1.bits.data,dataWidth,2,(_-_))

  val ins = GroupDecoupled(io.i0,io.i1)
  val outs = GroupDecoupled(io.sum,io.diff)
  SyncDecoupled(ins,outs)
}
