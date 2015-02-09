
name := """web"""

organization := "de.leanovate.dose"

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scaldi" %% "scaldi-play" % "0.4.1",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "react" % "0.11.1",
  "org.webjars" % "es5-shim" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.2.0",
  "com.github.kristofa" % "brave-zipkin-spancollector" % "2.2.1"
)

target in Universal := Option(System.getenv("DIST_DIR")).map(new File(_)).getOrElse(baseDirectory.value / ".." / "vagrant/dists")

val distDocker = TaskKey[Unit]("dist-docker", "Copies assembly jar")

distDocker <<= (NativePackagerKeys.dist in Universal, baseDirectory) map { (asm, base) => 
   val source = asm.getPath
   var target = (base / ".." / "docker/web/dist").getPath
   Seq("mkdir", "-p", target) !!;
   Seq("cp", source, target) !!
}