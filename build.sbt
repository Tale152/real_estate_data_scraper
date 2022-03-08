name := "real_estate_data_scraper"
version := "1.0"
scalaVersion := "2.12.10"

val rxScalaVersion = "0.27.0"

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % rxScalaVersion,
)
