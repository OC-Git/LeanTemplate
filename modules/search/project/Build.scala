import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "leantemplate_search"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        javaCore, javaJdbc, javaEbean, 
        "org.apache.lucene" % "lucene-core" % "3.6.0"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
