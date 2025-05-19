import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.4"

lazy val microservice = Project("ioss-intermediary-registration", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
  )
  .settings(PlayKeys.playDefaultPort := 10185)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .configs(Test)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)


lazy val testSettings = Defaults.testSettings ++ Seq(
  unmanagedSourceDirectories := Seq(
    baseDirectory.value / "test",
    baseDirectory.value / "test" / "testutils"
  ),
  parallelExecution := false,
  fork := true,
  javaOptions ++= Seq(
    "-Dlogger.resource=logback-test.xml"
  )
)
