import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by li on 16/9/8.
  */
object KeywordExtractorTest {


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KeywordExtractorTest").setMaster("local")
    val sc = new SparkContext(conf)

    val file = sc.textFile("/Users/li/kunyan/DataSet/textRankTestData/textRankTest2.txt")

    val doc = file.flatMap{row =>

      row.split(",").filter(word => word.length >= 2)
    }.collect().toList

    val keyWordList = KeywordExtractor.run("url", 5, doc, 10, 100, 0.85f)

    keyWordList.foreach {
      x => println(x._1, x._2)
    }

    sc.stop()

  }


}
