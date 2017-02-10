import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by li on 16/9/8.
  */
object KeywordExtractorTest {


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KeywordExtractorTest").setMaster("local")
    val sc = new SparkContext(conf)

    val file = sc.textFile("/Users/li/workshop/DataSet/textRankTestData/textRankTest2.txt")

    val docs = file.map{row =>

      row.split(",").filter(word => word.length >= 2)
    }

    val keyWordList = docs.map(doc => KeywordExtracto.keywordExtractor("url", 5, doc.toList, 10, 100, 0.85f))

    var i = 1

    keyWordList.foreach { doc => {
      println(s"第${i}篇文章的关键词")
      doc.foreach(x => print(s"${x._1}" + "\t"))
      println()
      i = i + 1
    }}

    sc.stop()
  }


}
