package AbstractExactor

import breeze.linalg.Vector
import org.apache.spark.mllib.feature.Word2VecModel
import org.graphstream.graph.implementations.SingleGraph

import scala.collection.mutable.ArrayBuffer

/**
  * Created by li on 2017/2/22.
  */
class ConstructSentenceGraph(val graphName: String,
                             val vectorSize: Int,
                             val sentenceList: Array[(Int, Array[String])],
                             val model: Word2VecModel) {


  private def textVectorsWithModel(text: Array[String]): Vector[Double] = {

    val wordVectors = Vector.zeros[Double](vectorSize)
    var docVectors = Vector.zeros[Double](vectorSize)
    var vector = Array[Double](vectorSize)
    var count = 0.0
    for (word <- text.indices) {

      try {
        vector = model.transform(text(word)).toArray
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

  private def textVecPair(textVec: Map[Int, Vector[Double]]): Array[((Int, Vector[Double]), (Int, Vector[Double]))] = {

    val group = new ArrayBuffer[((Int, Vector[Double]), (Int, Vector[Double]))]

    for (i <- 0 to textVec.size -2) {

      val arr = List.range(i + 1, textVec.keys.size)

      for (j <- arr) {

        val tmp = ((i, textVec(i)), (j, textVec(j)))
        group += tmp
      }

    }

    group.toArray
  }


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

    val correChange = correlation.map(x => (x._2, x._1, x._3))
    val finalCorrelation = correlation.union(correChange)

    println("最终的相似度为")
    finalCorrelation.foreach(x=> println(x._1, x._2, x._3))
    finalCorrelation
  }


  def constructGraph: SingleGraph = {
    val graph = new SingleGraph(graphName)

    val sentenceVec = sentenceList
      .map(x => (x._1, textVectorsWithModel(x._2))).toMap
    println("词向量为：")
    sentenceVec.foreach(x=> println(x._1, x._2))
    val pair  = textVecPair(sentenceVec)
    val edgeSet = cosineRelation(pair).map(x => (x._1.toString, x._2.toString, x._3))


    // 构建图的节点，每一个句子为一个节点
    sentenceList.foreach(
      sentence => if (graph.getNode(sentence._1) == null) graph.addNode(sentence._1.toString)
    )

    // 构建关键词图的边
    edgeSet.toList.sortBy(x => x._3).map(x => (x._1, x._2))
      .foreach {
      edges =>

        if (graph.getEdge(s"${edges._1}-${edges._2}") == null && graph.getEdge(s"${edges._2}-${edges._1}") == null) {
          graph.addEdge(s"${edges._1}-${edges._2}", edges._1, edges._2)

          None
        }
    }

    graph

  }
}