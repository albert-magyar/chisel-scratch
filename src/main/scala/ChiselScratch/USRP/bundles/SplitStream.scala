package ChiselScratch.USRP.bundles

import Chisel._
import ChiselScratch.USRP.common._

class SplitStreamIO(dataWidth: Int = 16) extends Bundle {
  val in = new DecoupledIO(new StreamPayload(dataWidth)).flip
  val o0 = new DecoupledIO(new StreamPayload(dataWidth))
  val o1 = new DecoupledIO(new StreamPayload(dataWidth))
}

class SplitStream(dataWidth: Int = 16) extends Module {
  val io = new SplitStreamIO(dataWidth)
  io.o0.bits := io.in.bits
  io.o1.bits := io.in.bits
  val outs_ready = io.o0.ready && io.o1.ready
  val int_valid = io.in.valid && outs_ready
  io.in.ready := outs_ready
  io.o0.valid := int_valid
  io.o1.valid := int_valid
}
