package com.atguigu.table

import com.atguigu.bean.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{EnvironmentSettings, Table}
import org.apache.flink.table.api.scala._

object TableAPITest {

  def main(args: Array[String]): Unit = {

    //1.创建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    //2.读取数据源
    val lineDStream: DataStream[String] = env.socketTextStream("hadoop102", 9999)

    //3.转换为样例类
    val sensorDStream: DataStream[SensorReading] = lineDStream.map(line => {
      val arr: Array[String] = line.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })

    // 基于env创建 tableEnv
    val settings: EnvironmentSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
    val tableEnv: StreamTableEnvironment = StreamTableEnvironment.create(env, settings)

    //4.根据DataStream创建一张表
    val table: Table = tableEnv.fromDataStream(sensorDStream)

    //5.使用TableAPI
    //    val result: DataStream[String] = table.select("id").toAppendStream[String]
    val result: DataStream[(String, Double)] = table.select('id, 'temperature).toAppendStream[(String, Double)]

    result.print()

    env.execute()


  }

}
