name := """das-alpha-hmrc-api-mock"""

lazy val `das-alpha-hmrc-api-mock` = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .enablePlugins(GitVersioning)
  .enablePlugins(GitBranchPrompt)

git.useGitDescribe := true

routesImport ++= Seq(
  "uk.gov.hmrc.domain._",
  "uk.gov.bis.levyApiMock.models.PlayBindings._",
  "uk.gov.bis.levyApiMock.models.QueryBinders._",
  "org.joda.time.LocalDate")

scalaVersion := "2.11.8"

PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")

resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies ++= Seq(
  ws,
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.17.0",
  "uk.gov.hmrc" %% "domain" % "3.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.typelevel" %% "cats" % "0.6.1",
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "com.github.melrief" %% "pureconfig" % "0.1.6",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test
)
