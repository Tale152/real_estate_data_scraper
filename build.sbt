name := "real_estate_data_scraper"
version := "1.0"
scalaVersion := "2.12.10"

val rxScalaVersion = "0.27.0"
val scalajHttpVersion = "2.4.2"
val htmlcleanerVersion = "2.26"

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % rxScalaVersion,
  "org.scalaj" %% "scalaj-http" % scalajHttpVersion,
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % htmlcleanerVersion,
)
