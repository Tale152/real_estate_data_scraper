name := "real_estate_data_scraper"
version := "1.0"
scalaVersion := "2.12.10"

val htmlcleanerVersion = "2.26"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % htmlcleanerVersion,
)
