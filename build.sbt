name := "textrank"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.4" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.1" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.7.1" % "provided" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.5.2" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.5.2" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "mysql" % "mysql-connector-java" % "3.1.14" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.graphstream" % "gs-core" % "1.1.2" excludeAll ExclusionRule(organization = "javax.servlet")

libraryDependencies += "org.apache.spark" % "spark-graphx_2.10" % "1.5.2" excludeAll ExclusionRule(organization = "javax.servlet")


assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("javax", "el", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


test in assembly := {}

    