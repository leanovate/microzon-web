
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

val debian = TaskKey[Unit]("debian", "Create debian package")

debian <<= (NativePackagerKeys.dist in Universal, baseDirectory, version) map { (distOut, base, ver) =>
  val bintrayUser = System.getenv("BINTRAY_USER")
  val bintrayKey = System.getenv("BINTRAY_KEY")
  val release = ver + "-" + System.getenv("TRAVIS_BUILD_NUMBER")
  val debOut = (base / "target" / "microzon-web.deb")
  val debBase = (base / "target" / "deb")
  IO.unzip(distOut, debBase / "opt")
  IO.move(debBase / "opt" / s"web-$ver", debBase / "opt" / "web")
  IO.write(debBase / "DEBIAN" / "control",
    s"""Package: microzon-web
    |Version: $release
    |Section: misc
    |Priority: extra
    |Architecture: all
    |Depends: supervisor, oracle-java8-installer
    |Maintainer: Bodo Junglas <landru@untoldwind.net>
    |Homepage: http://github.com/leanovate/microzon
    |Description: Web facade service
    |""".stripMargin)
  s"/usr/bin/fakeroot /usr/bin/dpkg-deb -b ${debBase.getPath} ${debOut.getPath}" !;
  s"/usr/bin/curl -T ${debOut.getPath} -u${bintrayUser}:${bintrayKey} https://api.bintray.com/content/untoldwind/deb/microzon/${ver}/pool/main/m/microzon/microzon-web-${release}_all.deb;deb_distribution=trusty;deb_component=main;deb_architecture=all?publish=1" !
}