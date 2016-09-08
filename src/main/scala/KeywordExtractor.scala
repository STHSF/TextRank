import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.SingleGraph

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by li on 16/6/24.
  * 使用TextRank进行关键词提取
  */
object KeywordExtractor {

  /**
    * 构建候选关键词图
    *
    * @param graphName 图名字
    * @param window 窗口大小
    * @param segWord 分词结果
    * @return 关键词图
    * @author Li Yu
    */
  def keywordGraphConstruction(graphName: String,
                               window: Int,
                               segWord: ListBuffer[String]): SingleGraph = {

    val graph = new SingleGraph(graphName)

    // 获取文本网络的节点
    segWord.foreach {
      word => if (graph.getNode(word) == null) graph.addNode(word)
    }

    // 导入分完词的数据,并通过设置的窗口截取
    var wordSeg = new ListBuffer[(ListBuffer[(String)])]

    val num = segWord.size - window

    for (i <- 0 to num) {

      val item = new ListBuffer[(String)]

      for (j <- 0 until window) {

        item += segWord(i + j)

      }

      wordSeg += item
    }

    // 获取每个顶点以及所包含的窗口内的邻居节点
    val wordSet = segWord.toSet

    val edgeSet = wordSet.map {
      word => {
        val edgeList = new mutable.HashSet[(String)]
        wordSeg.foreach {
          list => {
            if (list.contains(word)){
              list.foreach(x => edgeList.+=(x))
            }
          }
        }
        (word, edgeList -= word)
      }
    }

    // 构建关键词图的边
    edgeSet.toArray.foreach {
      edge => {
        edge._2.toList.foreach {
          edges =>

            if (graph.getEdge(s"${edge._1}-$edges") == null &&
              graph.getEdge(s"$edges-${edge._1}") == null) {
              graph.addEdge(s"${edge._1}-$edges", edge._1, edges)
              None
            }
        }
      }
    }

    graph
  }

  /**
    * 关键词提取, 输出个文章提取的关键词, 无向图名称为文章的url
    *
    * @param graph 关键词节点图
    * @param keywordNum 关键词个数
    * @param iterator textRank迭代次数
    * @param df 阻尼系数(Damping Factor)
    * @return
    */

  def KeywordsExtract(graph: SingleGraph, keywordNum: Int, iterator: Int, df: Float) = {

    val nodes = graph.getNodeSet.toArray.map(_.asInstanceOf[Node])
    val scoreMap = new mutable.HashMap[String, Float]

    // 节点权重初始化
    nodes.foreach(node => scoreMap.put(node.getId, 1.0f))

    // 迭代 迭代传播各节点的权重，直至收敛。
    (1 to iterator).foreach {
      i =>
        nodes.foreach {
          node =>
            val edges = node.getEdgeSet.toArray.map(_.asInstanceOf[Edge])
            var score = 1.0f - df
            edges.foreach {
              edge =>
                val node0 = edge.getNode0.asInstanceOf[Node]
                val node1 = edge.getNode1.asInstanceOf[Node]
                val tempNode = if (node0.getId.equals(node.getId)) node1 else node0

                score += df * (1.0f * scoreMap(tempNode.getId) / tempNode.getDegree)
            }
            scoreMap.put(node.getId, score)
        }
    }

    // 对节点权重进行倒序排序，从而得到最重要的num个单词，作为候选关键词。
    scoreMap.toList.sortWith(_._2 > _._2).slice(0, keywordNum)
  }

  /**
    * 执行函数, 基于textRank的关键词提取方法
    *
    * @param graphName 图标识
    * @param window 词窗口大小
    * @param doc 待提取文本
    * @param keywordNum 提取关键词的个数
    * @param iterator textRank迭代次数
    * @param df 阻尼系数
    * @return 关键词, 得分
    */
  def run(graphName: String, window: Int, doc: ListBuffer[String],
          keywordNum: Int, iterator: Int, df: Float): List[(String, Float)] = {

    // 生成关键词图
    val textGraph = keywordGraphConstruction(graphName, window, doc)

    // 提取关键词
    val result = KeywordsExtract(textGraph, keywordNum, iterator, df)

    result
  }

}