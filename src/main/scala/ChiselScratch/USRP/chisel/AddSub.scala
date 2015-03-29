package ChiselScratch.USRP.chisel

import Chisel._
import ChiselScratch.USRP.common._

class AddSub(dataWidth: Int = 16) extends Module {
  val io = new AddSubIO(dataWidth)
  val sum_q = Module(new Queue(new StreamPayload(2*dataWidth),16))
  val diff_q = Module(new Queue(new StreamPayload(2*dataWidth),16))

  sum_q.io.enq.bits.last := io.i0.bits.last
  diff_q.io.enq.bits.last := io.i0.bits.last
  sum_q.io.enq.bits.data := PackedSIMD(io.i0.bits.data,io.i1.bits.data,
    dataWidth,2,(_+_))
  diff_q.io.enq.bits.data := PackedSIMD(io.i0.bits.data,io.i1.bits.data,
    dataWidth,2,(_-_))
  SyncDecoupled(GroupDecoupled(io.i0,io.i1),GroupDecoupled(sum_q.io.enq,diff_q.io.enq))

  io.sum.bits := sum_q.io.deq.bits
  io.diff.bits := diff_q.io.deq.bits
  SyncDecoupled(GroupDecoupled(sum_q.io.deq,diff_q.io.deq),
    GroupDecoupled(io.sum,io.diff))
}
