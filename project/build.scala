import sbt._
import Keys._

object BuildSettings
{
  val buildOrganization = "edu.berkeley.cs"
  val buildVersion = "1.1"
  val buildScalaVersion = "2.10.1"

  def apply() = {
    Defaults.defaultSettings ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      scalacOptions ++= Seq( "-Xmax-classfile-name", "200" )
    )
  }
}

object ChiselBuild extends Build
{
  import BuildSettings._
  lazy val scratch = Project("scratch", file("."), settings = BuildSettings()) dependsOn(chisel)
  lazy val chisel = Project("chisel", file("chisel"), settings = BuildSettings())
}
