// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.19")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.7")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")