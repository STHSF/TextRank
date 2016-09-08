import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by li on 16/9/8.
  */
object KeywordExtractorTest {


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("textRank").setMaster("local")
    val sc = new SparkContext(conf)

    val file = sc.textFile("/Users/li/kunyan/DataSet/textRankTestData/textRankTest2.txt")
    val doc = new ListBuffer[(String)]

    file.foreach {
      word =>
        val list = word.split(",").foreach(x => doc.+=(x))
    }

    val keyWordList = KeywordExtractor.run("url", 5, doc, 3, 100, 0.85f)

    keyWordList.foreach {
      x => println(x._1, x._2)
    }

    sc.stop()

  }


}
