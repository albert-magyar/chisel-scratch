class AddSub(width: Int = 16) extends Module {
  val io = new AddSubIO(width)
  
  val sum = PackedSIMD(io.i0.data,io.i1.data,(_+_))
  val diff = PackedSIMD(io.i0.data,io.i1.data,(_-_))
  val result = Cat(sum,diff)


}