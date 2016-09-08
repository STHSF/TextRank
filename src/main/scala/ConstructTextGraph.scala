import org.graphstream.graph.implementations.SingleGraph

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by li on 16/6/23.
  */

/**
  * 构建候选关键词图
 *
  * @param graphName 图标识s
  * @param winSize 窗口大小
  * @param segWord 分词的结果
  * @return 候选关键词图
  * @author LiYu
  */
class ConstructTextGraph(val graphName: String, val winSize: Int, val segWord: ListBuffer[String]) {

  /**
    * 构建候选关键词图
 *
    * @return 候选关键词图
    */
  def constructGraph: SingleGraph = {

    val graph = new SingleGraph(graphName)

    // 获取文本网络的节点
    segWord.foreach(
      word => if (graph.getNode(word) == null) graph.addNode(word)
    )

    // 导入分完词的数据,并通过设置的窗口截取
    var wordSeg = new ListBuffer[(ListBuffer[(String)])]

    val num = segWord.size - winSize

    for (i <- 0 to num) {

      val item = new ListBuffer[(String)]

      for (j <- 0 until winSize) {

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

            if (graph.getEdge(s"${edge._1}-${edges}") == null &&
              graph.getEdge(s"${edges}-${edge._1}") == null) {
              graph.addEdge(s"${edge._1}-${edges}", edge._1, edges)
              None
            }
        }
      }
    }

    graph

  }
}
