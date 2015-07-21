package ChiselScratch

import Chisel._

object Main {
  def main(args: Array[String]): Unit = {
    val rArgs  = args.slice(1, args.length)

    args(0) match {
      case "Vscale" =>
        chiselMain(rArgs, () => Module(new Vscale()))
      case "TreePLRUTester" =>
        chiselMainTest(rArgs, () => Module(new TreePLRUTop(2,4))){
          c => new TreePLRUTester(c)
        }
      case "VLSTreePLRUTester" =>
        chiselMainTest(rArgs, () => Module(new VLSTreePLRUTop(2,4))){
          c => new VLSTreePLRUTester(c)
        }
      case "USRP.common.SplitDecoupledExampleTester" =>
        chiselMainTest(rArgs, () => Module(new USRP.common.SplitDecoupledExample(10))){
          c => new USRP.common.SplitDecoupledExampleTester(c)
        }
      case "USRP.chisel.AddSub" =>
        chiselMain(rArgs, () => Module(new USRP.chisel.AddSub()))
      case "USRP.chisel.AddSubWrapper" =>
        chiselMain(rArgs, () => Module(new USRP.chisel.AddSubWrapper()))
      case "USRP.chisel.AddSubComplex" =>
        chiselMain(rArgs, () => Module(new USRP.chisel.AddSubComplex()))
      case "USRP.basic.AddSub" =>
        chiselMain(rArgs, () => Module(new USRP.basic.AddSub()))
      case "USRP.basic.AddSubWrapper" =>
        chiselMain(rArgs, () => Module(new USRP.basic.AddSubWrapper()))
      case "USRP.bundles.AddSub" =>
        chiselMain(rArgs, () => Module(new USRP.bundles.AddSub()))
      case "USRP.bundles.AddSubWrapper" =>
        chiselMain(rArgs, () => Module(new USRP.bundles.AddSubWrapper()))
      case "RepeatedLBarrelShift" =>
        chiselMain(rArgs, () => Module(new RepeatedLBarrelShift(33)))
    }
  }
}

