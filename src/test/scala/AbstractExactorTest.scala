import breeze.linalg.Vector
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by li on 2017/3/7.
  */
object AbstractExactorTest {


  def vectorPair(sentenceVec: Map[Int, Vector[Double]]): Array[((Int, Vector[Double]), (Int, Vector[Double]))] ={

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
    group.toArray
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KeywordExtractorTest").setMaster("local")
    val sc = new SparkContext(conf)

    val colors = Map(0 -> Vector(1.0,2.0,3.0,4.0),
      1 -> Vector(1.0,2.0,3.0,4.0),
      2 -> Vector(1.0,2.0,3.0,4.0),
      3 -> Vector(1.0,2.0,3.0,4.0),
      4 -> Vector(1.0,2.0,3.0,4.0),
      5 -> Vector(1.0,2.0,3.0,4.0),
      6 -> Vector(1.0,2.0,3.0,4.0))

    val ans = vectorPair(colors)
    ans.foreach(x => println(x.toString))

  }

}
