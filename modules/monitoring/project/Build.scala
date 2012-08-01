import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "leantemplate.monitoring"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "commons-io" % "commons-io" % "2.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
