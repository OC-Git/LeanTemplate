import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "leantemplate_crud-view"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      javaCore, javaJdbc, javaEbean, 
      "leantemplate_commons" % "leantemplate_commons_2.10" % "1.0-SNAPSHOT",
      "leantemplate_crud" % "leantemplate_crud_2.10" % "1.0-SNAPSHOT"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
