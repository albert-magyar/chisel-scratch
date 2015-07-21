BASE = src/main/scala/ChiselScratch/USRP
DEPS = $(BASE)/basic/*.scala $(BASE)/chisel/*.scala $(BASE)/bundles/*.scala $(BASE)/common/*.scala

default: generated-src/AddSubWrapper.v generated-src/AddSubComplex.v generated-src/Vscale.v

generated-src/AddSubWrapper.v: $(DEPS)
	sbt "run USRP.basic.AddSubWrapper --backend v --targetDir generated-src"

generated-src/AddSubComplex.v: $(DEPS)
	sbt "run USRP.chisel.AddSubComplex --backend v --targetDir generated-src"

generated-src/Vscale.v: $(DEPS)
	sbt "run Vscale --backend v --targetDir generated-src"

.PHONY: islands

islands:
	sbt "run USRP.chisel.AddSubComplex --backend c --targetDir test-islands --partitionIslands"
