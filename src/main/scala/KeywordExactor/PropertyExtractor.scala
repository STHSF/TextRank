package KeywordExactor

import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.graph.{Edge, Node}

import scala.collection.mutable

/**
  * Created by li on 16/6/24.
  */

/**
  * 关键词提取, 输出个文章提取的关键词, 无向图名称为文章的url
  *
  * @param graph 节点图
  * @param keywordNum 关键词个数
  * @return 文本的关键词
  * @author LiYu
  */
class PropertyExtractor(val graph: SingleGraph, val keywordNum: Int) {

  /**
    *
    * @param iterator textRank迭代次数
    * @param df 阻尼系数(Damping Factor)
    * @return 关键词和得分
    */
  // 使用textRank提取关键词
  def textrank(iterator: Int, df: Float) = {

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
}