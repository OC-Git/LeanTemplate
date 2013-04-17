import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "leantemplate_abtest"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        javaCore, javaJdbc, javaEbean, 
        "leantemplate_crud" % "leantemplate_crud_2.10" % "1.0-SNAPSHOT"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
