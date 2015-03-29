package ChiselScratch.USRP.common

import Chisel._

class AddSubIO(dataWidth: Int = 16) extends Bundle {
  val i0 = new DecoupledIO(new StreamPayload(2*dataWidth)).flip
  val i1 = new DecoupledIO(new StreamPayload(2*dataWidth)).flip
  val sum = new DecoupledIO(new StreamPayload(2*dataWidth))
  val diff = new DecoupledIO(new StreamPayload(2*dataWidth))
}

class AddSubComplexIO(dataWidth: Int = 16) extends Bundle {
  val i0 = new DecoupledIO(Sample(dataWidth)).flip
  val i1 = new DecoupledIO(Sample(dataWidth)).flip
  val sum = new DecoupledIO(Sample(dataWidth))
  val diff = new DecoupledIO(Sample(dataWidth))
}
