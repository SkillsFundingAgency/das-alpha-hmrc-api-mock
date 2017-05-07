

name := """das-alpha-hmrc-api-mock"""

enablePlugins(PlayScala)
disablePlugins(PlayLayoutPlugin)

enablePlugins(GitVersioning)
enablePlugins(GitBranchPrompt)

git.useGitDescribe := true

enablePlugins(BuildInfoPlugin)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "uk.gov.bis.levyApiMock.buildinfo"
buildInfoOptions ++= Seq(BuildInfoOption.ToJson, BuildInfoOption.BuildTime)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

routesImport ++= Seq(
  "uk.gov.hmrc.domain._",
  "uk.gov.bis.levyApiMock.models.PlayBindings._",
  "uk.gov.bis.levyApiMock.models.QueryBinders._",
  "org.joda.time.LocalDate")

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-language:higherKinds",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")

javaOptions := Seq(
  "-Dconfig.file=src/main/resources/development.application.conf",
  "-Dlogger.file=src/main/resources/development.logger.xml"
)

resolvers += Resolver.bintrayRepo("hmrc", "releases")

val enumeratumVersion = "1.5.12"

libraryDependencies ++= Seq(
  ws,
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.0.0",
  "uk.gov.hmrc" %% "domain" % "3.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.typelevel" %% "cats-core" % "0.8.1",
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "com.github.melrief" %% "pureconfig" % "0.1.6",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.beachape" %% "enumeratum-play" % "1.5.11",

  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
