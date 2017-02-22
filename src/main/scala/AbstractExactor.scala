
import org.apache.spark.mllib.feature.Word2VecModel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by li on 2017/2/22.
  */
object AbstractExactor {

  def run(graphName: String,
          vectorSize: Int,
          sentenceList: Array[(Int, Array[String])],
          keywordNum: Int,
          iterator: Int,
          model: Word2VecModel,
          df: Float): List[(String, Float)] = {

    // 生成关键词图
    val constructTextGraph = new ConstructSentenceGraph(graphName, vectorSize, sentenceList, model)
    val textGraph = constructTextGraph.constructGraph

    // 输出提取的关键词
    val keywordExtractor = new PropertyExtractor(textGraph, keywordNum)
    val result = keywordExtractor.extractKeywords(iterator, df)

    result
  }


  def main (args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)

    val data = sc.textFile("/Users/li/workshop/MyRepository/TextRank/src/main/resources/2.txt")
      .flatMap(_.split("。")).collect.map(x=> x.split(" "))

    val dataIndex = data.zipWithIndex.map(x=>(x._2, x._1))
    dataIndex.foreach(x=> println(x._1, x._2.mkString("")))
    val path = "hdfs://61.147.114.85:9000/home/liyu/word2vec/model2/10_100_5_102017-02-08-word2VectorModel/"

    val model = Word2VecModel.load(sc, path)
    val result = run("hehe", 100, dataIndex, 100, 100, model, 0.9F)
    println(result)


  }

}
