class WithLast[+T <: Data](gen: T) extends Bundle {
  val data = gen.clone.asOutput
  val last = Bool(OUTPUT)
}

object WithLast {
  def apply[T <: Data](gen: T): WithLast[T] = new WithLast(gen)
}

class Axi4Stream[+T <: Data](gen: T) extends DecoupledIO(WithLast(gen)) {
  def last: Bool = bits.last
  def data: T = bits.data
}

object PackedSIMD {
  def apply(x: Node, y: Node, elementWidth: Int, f: (Node, Node) => Node): Node {
    var result = Seq.empty[Node]
    assert(x.width == y.width)
    assert(x.width % elementWidth == 0)
    for (i <- 0 until x.width / elementWidth) {
      x_elem = x((i+1)*elementWidth-1,i*elementWidth)
      y_elem = y((i+1)*elementWidth-1,i*elementWidth)
      result = result ++ f(x_elem,y_elem)
    }
    Cat(result)
  }
}

class AddSubIO(width: Int = 16) extends Bundle {
  val i0 = Axi4Stream(Bits(width=width*2)).flip
  val i1 = Axi4Stream(Bits(width=width*2)).flip
  val sum = Axi4Stream(Bits(width=width*2))
  val diff = Axi4Stream(Bits(width=width*2))
}

class SplitStreamFIFO extends Module {

}

