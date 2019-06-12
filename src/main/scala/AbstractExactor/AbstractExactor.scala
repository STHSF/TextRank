package AbstractExactor

import KeywordExactor.PropertyExtractor
import org.apache.spark.mllib.feature.Word2VecModel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by li on 2017/2/22.
  */
object AbstractExactor {

  /**
    * 关键句提取运行主程序。
    * @param graphName 图名字
    * @param vectorSize 词向量的长度
    * @param sentenceList 句子组成的数据
    * @param keySentenceNum 关键句的个数
    * @param iterator textrank迭代的次数
    * @param word2vecModel 词向量模型
    * @param df df值
    * @return
    */
  def run(graphName: String,
          vectorSize: Int,
          sentenceList: Array[(Int, Array[String])],
          keySentenceNum: Int,
          iterator: Int,
          word2vecModel: Word2VecModel,
          df: Float): List[(String, Float)] = {

    // 生成关键词图
    val constructTextGraph = new ConstructSentenceGraph(graphName, vectorSize, sentenceList, word2vecModel)
    val textGraph = constructTextGraph.constructGraph

    // 输出提取的关键词
    val keywordExtractor = new PropertyExtractor(textGraph, keySentenceNum)
    val result = keywordExtractor.textrank(iterator, df).sortBy(_._1)

    result
  }


  def main (args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("AbstractExtractor")
    val sc = new SparkContext(conf)

    val filePath = args(0)
    val word2VecModelPath = args(1)

//    val data = sc.textFile("/Users/li/workshop/MyRepository/TextRank/src/main/resources/2.txt").flatMap(_.split("。")).collect.map(x=> x.split(" "))
    val data = sc.textFile(filePath).flatMap(_.split("。")).collect.map(x=> x.split(" "))


    val dataIndex = data.zipWithIndex.map(x=>(x._2, x._1))
    dataIndex.foreach(x=> println(x._1, x._2.mkString("")))
    // val word2VecModelPath = "hdfs://61.147.114.85:9000/home/liyu/word2vec/model2/10_100_5_102017-02-08-word2VectorModel"
//    val word2VecModelPath = "/Users/li/workshop/DataSet/word2vec/model-10-100-20/2016-08-16-word2VectorModel/"

    val model = Word2VecModel.load(sc, word2VecModelPath)
    val da = model.findSynonyms("共产党", 2)
    da.foreach(x => println(x))

    val result = run("jiji", 100, dataIndex, 2, 100, model, 0.9F)
    println(result)

    // 转换成句子
    val index = result.map(x=> x._1)
    for (elem <- index) {
      print(dataIndex(elem.toInt)._2.mkString(""))
    }
  }

}
