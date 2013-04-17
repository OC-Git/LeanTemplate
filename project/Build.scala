import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "LeanTemplate"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        javaCore, javaJdbc, javaEbean, 
        
        "postgresql" % "postgresql" % "9.1-901.jdbc4",
        "commons-lang" % "commons-lang" % "2.6",
        "commons-collections" % "commons-collections" % "3.2.1",
        "commons-codec" % "commons-codec" % "1.4",
        "commons-io" % "commons-io" % "2.3", 
        "org.apache.commons" % "commons-email" % "1.1",
        "funcy" % "funcy_2.9.1" % "0.1" % "test",
        "org.jsoup" % "jsoup" % "1.6.2" % "test", 
        "net.sf.opencsv" % "opencsv" % "2.1",
        
        // modules
        "leantemplate_commons" % "leantemplate_commons_2.10" % "1.0-SNAPSHOT",
        "leantemplate_search" % "leantemplate_search_2.10" % "1.0-SNAPSHOT",
        "leantemplate_crud" % "leantemplate_crud_2.10" % "1.0-SNAPSHOT",
        "leantemplate_crud-view" % "leantemplate_crud-view_2.10" % "1.0-SNAPSHOT",
        "leantemplate_abtest" % "leantemplate_abtest_2.10" % "1.0-SNAPSHOT",
        "leantemplate_monitoring" % "leantemplate_monitoring_2.10" % "1.0-SNAPSHOT"
    )

    // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory 
	def customLessEntryPoints(base: File): PathFinder = ( 
	    (base / "app" / "assets" / "admin" / "bootstrap" / "less" * "bootstrap.less") +++
	    (base / "app" / "assets" / "admin" / "bootstrap" / "less" * "responsive.less") +++ 
	    (base / "app" / "assets" / "admin" / "stylesheets" * "*.less") +++
	    (base / "app" / "assets" / "site" / "bootstrap" / "less" * "bootstrap.less") +++
	    (base / "app" / "assets" / "site" / "bootstrap" / "less" * "responsive.less") +++ 
	    (base / "app" / "assets" / "site" / "stylesheets" * "*.less")
	)
	
	val main = play.Project(appName, appVersion, appDependencies).settings(
	  // Add your own project settings here
	    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
	    resolvers += Resolver.url("Sebastian Baltes Repository", url("http://sebastianbaltesobjectcode.github.com/releases/"))(Resolver.ivyStylePatterns),
	    resolvers += Resolver.url("Joerg Violas Repository", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
	)    

}
