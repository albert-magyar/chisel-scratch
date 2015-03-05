package ChiselScratch

import Chisel._

class AddSubIO(width: Int = 16) extends Bundle {
  val i0 = new Axi4Stream(Bits(width=width*2)).flip
  val i1 = new Axi4Stream(Bits(width=width*2)).flip
  val sum = new Axi4Stream(Bits(width=width*2))
  val diff = new Axi4Stream(Bits(width=width*2))
}

class AddSub(width: Int = 16) extends Module {
  val io = new AddSubIO(width)
  
  val sum = PackedSIMD(io.i0.data,io.i1.data,(_+_))
  val diff = PackedSIMD(io.i0.data,io.i1.data,(_-_))
  val result = Cat(sum,diff)


}
