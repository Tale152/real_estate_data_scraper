name := "real_estate_data_scraper"
version := "1.0"
scalaVersion := "2.12.10"

val htmlcleanerVersion = "2.26"
val scalajHttpVersion = "2.4.2"
val gsonVersion = "2.9.0"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % htmlcleanerVersion,
  "org.scalaj" %% "scalaj-http" % scalajHttpVersion,
  "com.google.code.gson" % "gson" % gsonVersion,
)
