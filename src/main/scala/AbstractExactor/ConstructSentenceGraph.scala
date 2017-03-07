package AbstractExactor

import breeze.linalg.Vector
import org.apache.spark.mllib.feature.Word2VecModel
import org.graphstream.graph.implementations.SingleGraph

import scala.collection.mutable.ArrayBuffer

/**
  * Created by li on 2017/2/22.
  *
  * 构建关键句图
  *
  * @param graphName 关键句图的名称
  * @param vectorSize 词向量的长度
  * @param sentenceList 关键句数组，[(SentenceID,  句子分词后的组成的词组)]
  * @param word2VecModel 词向量模型
  */
class ConstructSentenceGraph(val graphName: String,
                             val vectorSize: Int,
                             val sentenceList: Array[(Int, Array[String])],
                             val word2VecModel: Word2VecModel) {
  /**
    * 构建句向量
    *
    * @param sentence
    * @return
    */
  private def sentenceVectorsWithModel(sentence: Array[String]): Vector[Double] = {

    val wordVectors = Vector.zeros[Double](vectorSize)
    var docVectors = Vector.zeros[Double](vectorSize)
    var vector = Array[Double](vectorSize)
    var count = 0.0
    for (word <- sentence.indices) {

      try {
        vector = word2VecModel.transform(sentence(word)).toArray
      }
      catch {
        case e: Exception => vector = Vector.zeros[Double](vectorSize).toArray
      }

      val tmp = Vector.apply(vector)
      wordVectors.+=(tmp)
      count += 1.0
    }

    if(count != 0) {
      println("构建文本向量，词的个数为")
      println(count)
      docVectors = wordVectors./=(count)
    }

    docVectors
  }

//  /**
//    * 生成句子对
//    *
//    * @param textVec 句向量所组成的集合
//    * @return 句子对
//    */
//  private def textVecPair(textVec: Map[Int, Vector[Double]]): Array[((Int, Vector[Double]), (Int, Vector[Double]))] = {
//
//    val group = new ArrayBuffer[((Int, Vector[Double]), (Int, Vector[Double]))]
//
//    for (i <- 0 to textVec.size -2) {
//
//      val arr = List.range(i + 1, textVec.keys.size)
//
//      for (j <- arr) {
//
//        val tmp = ((i, textVec(i)), (j, textVec(j)))
//        group += tmp
//      }
//
//    }
//    println("group size", group.size)
//
//    group.toArray
//  }

  /**
    * 生成句子对
    *
    * @param sentenceVec 句向量所组成的集合
    * @return 句子对
    */
  private def vectorPair(sentenceVec: Map[Int, Vector[Double]]): Array[((Int, Vector[Double]), (Int, Vector[Double]))] ={

    val group = new ArrayBuffer[((Int, Vector[Double]), (Int, Vector[Double]))]

    val nums = Array.range(0, sentenceVec.size)

    // 产生组合对
    val combinations = nums.combinations(2).toList

    combinations.foreach{
      x =>{
        val a = sentenceVec(x.head)
        val b = sentenceVec(x(1))
        group.+=(((x.head, a), (x(1), b)))

      }
    }

    println("group size", group.size)
    group.toArray
  }

  /**
    * 计算两个字／词／句向量的余弦值
    *
    * @param vec1 字／词／句向量1
    * @param vec2 字／词／句向量2
    * @return 两个向量的余弦值
    */
  private def cosineCorr(vec1: scala.collection.immutable.Vector[Double],
                         vec2: scala.collection.immutable.Vector[Double]): Double = {

    val member = vec1.zip(vec2).map(x => x._1 * x._2).sum
    val tmp1 =  math.sqrt(vec1.map(num => {math.pow(num, 2)}).sum)
    val tmp2 =  math.sqrt(vec2.map(num => {math.pow(num, 2)}).sum)

    member / (tmp1 * tmp2)
  }


  private def cosineRelation(pair: Array[((Int, Vector[Double]), (Int, Vector[Double]))]): Array[(Int, Int, Double)] ={

    val correlation = pair.map(line => {

      val vec1 = line._1._2.toArray.toVector
      val vec2 = line._2._2.toArray.toVector
      val corr = cosineCorr(vec1, vec2)

      (line._1._1, line._2._1, corr)
    })

//    val correChange = correlation.map(x => (x._2, x._1, x._3))
//    val finalCorrelation = correlation.union(correChange)

    println("最终的相似度为")
    correlation.foreach(x=> println(x._1, x._2, x._3))
    correlation
  }


  def constructGraph: SingleGraph = {
    val graph = new SingleGraph(graphName)

    val sentenceVec = sentenceList
      .map(x => (x._1, sentenceVectorsWithModel(x._2))).toMap
    println("词向量为：")
    sentenceVec.foreach(x=> println(x._1, x._2))
    val pair  = vectorPair(sentenceVec)
    val edgeSet = cosineRelation(pair).map(x => (x._1.toString, x._2.toString, x._3))

    // 构建图的节点，每一个句子为一个节点
    sentenceList.foreach(
      sentence => if (graph.getNode(sentence._1) == null) graph.addNode(sentence._1.toString)
    )

    // 构建关键词图的边，如果两个节点的余弦相似度高于某一个阈值，则这两个节点之间存在一条边。
    edgeSet.toList.filter(_._3 > 0.78).map(x => (x._1, x._2)).foreach {
        edges =>
          if (graph.getEdge(s"${edges._1}-${edges._2}") == null && graph.getEdge(s"${edges._2}-${edges._1}") == null) {
            graph.addEdge(s"${edges._1}-${edges._2}", edges._1, edges._2)
            None
          }
      }

    graph
  }
}