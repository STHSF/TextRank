import scala.collection.mutable.ListBuffer

/**
  * Created by li on 16/6/24.
  */
object TextRank {
  /**
    *
    * @param graphName 图标识
    * @param window 词窗口大小
    * @param doc 待抽取文本
    * @param keywordNum 提取关键词个数
    * @param iterator textRank迭代次数
    * @param df 阻尼系数
    * @return 关键词, 得分
    */
  def run(graphName: String, window: Int, doc: ListBuffer[String],
          keywordNum: Int, iterator: Int, df: Float): List[(String, Float)] = {

    // 生成关键词图
    val constructTextGraph = new ConstructTextGraph(graphName, window, doc)
    val textGraph = constructTextGraph.constructGraph

    // 输出提取的关键词
    val keywordExtractor = new PropertyExtractor(textGraph, keywordNum)
    val result = keywordExtractor.extractKeywords(iterator, df)

    result
  }

}
