import mill._, mill.scalalib._, mill.scalalib.publish._, mill.scalajslib._
import mill.scalalib.scalafmt._
import coursier.maven.MavenRepository
import ammonite.ops._

val thisPublishVersion = "0.2.0-SNAPSHOT"

val scalaVersions = List("2.12.8")
val thisScalaJSVersion = "0.6.27"

val macroParadiseVersion = "2.1.0"
val kindProjectorVersion = "0.9.4"

// cats libs -- make sure versions match up
val jjmVersion = "0.1.0-SNAPSHOT"
// val catsVersion = "1.6.1"
val scalajsReactVersion = "1.4.2"

class RadhocModule(val crossScalaVersion: String) extends CrossScalaModule with ScalaJSModule with ScalafmtModule with PublishModule {

  def scalaJSVersion = thisScalaJSVersion

  def scalacOptions = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-Ypartial-unification",
  )

  def ivyDeps = Agg(
    ivy"org.julianmichael::jjm-core::$jjmVersion",
    // ivy"org.typelevel::cats-core::$catsVersion",
    ivy"com.github.japgolly.scalajs-react::core::$scalajsReactVersion",
    ivy"com.github.japgolly.scalajs-react::extra::$scalajsReactVersion",
  )

  def artifactName = "radhoc"

  def publishVersion = thisPublishVersion

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "org.julianmichael",
    url = "https://github.com/julianmichael/radhoc",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("julianmichael", "radhoc"),
    developers = Seq(
      Developer("julianmichael", "Julian Michael", "https://github.com/julianmichael")
    )
  )
}

object radhoc extends Cross[RadhocModule](scalaVersions: _*)
