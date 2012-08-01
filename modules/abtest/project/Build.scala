import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "leantemplate.abtest"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "leantemplate.crud" % "leantemplate.crud_2.9.1" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
