import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "leantemplate_monitoring"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        javaCore, javaJdbc, javaEbean, 
       "commons-io" % "commons-io" % "2.3",
       "net.sf.opencsv" % "opencsv" % "2.3"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
