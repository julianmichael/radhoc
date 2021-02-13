import mill._, mill.scalalib._, mill.scalalib.publish._, mill.scalajslib._
import mill.scalalib.scalafmt._
import coursier.maven.MavenRepository
import ammonite.ops._

val thisPublishVersion = "0.4.0-SNAPSHOT"

val scalaVersions = List(
  "2.12.12",
  // "2.13.4"
  )
val thisScalaJSVersion = "1.4.0"

val macroParadiseVersion = "2.1.1"
val kindProjectorVersion = "0.11.3"

// cats libs -- make sure versions match up
val jjmVersion = "0.2.0-SNAPSHOT"
val scalajsReactVersion = "1.7.7"

class RadhocModule(val crossScalaVersion: String) extends CrossScalaModule with ScalaJSModule with ScalafmtModule with PublishModule {

  def scalaJSVersion = thisScalaJSVersion

  def scalacOptions = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-Ypartial-unification",
  )

  def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"org.scalamacros:::paradise:$macroParadiseVersion",
    ivy"org.typelevel:::kind-projector:$kindProjectorVersion"
  )

  def ivyDeps = Agg(
    ivy"org.julianmichael::jjm-core::$jjmVersion",
    ivy"com.github.japgolly.scalajs-react::core::$scalajsReactVersion",
    ivy"com.github.japgolly.scalajs-react::extra::$scalajsReactVersion",
    ivy"com.github.japgolly.scalajs-react::ext-monocle-cats::$scalajsReactVersion",
    ivy"com.github.japgolly.scalajs-react::ext-cats::$scalajsReactVersion",
    // ivy"com.github.japgolly.scalacss::core::$scalajsScalaCSSVersion",
    // ivy"com.github.japgolly.scalacss::ext-react::$scalajsScalaCSSVersion"
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
