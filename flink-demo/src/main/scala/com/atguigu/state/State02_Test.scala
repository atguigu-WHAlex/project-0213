//package com.atguigu.state
//
//import java.util
//
//import org.apache.flink.api.common.functions.{ReduceFunction, RichMapFunction}
//import org.apache.flink.api.common.state._
//import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
//import org.apache.flink.runtime.state.filesystem.FsStateBackend
//import org.apache.flink.runtime.state.memory.MemoryStateBackend
//import org.apache.flink.streaming.api.CheckpointingMode
//import org.apache.flink.streaming.api.checkpoint.ListCheckpointed
//import org.apache.flink.streaming.api.scala._
//
//object State02_Test {
//
//  def main(args: Array[String]): Unit = {
//
//    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
//
//    //配置状态后端
//    env.setStateBackend(new MemoryStateBackend())
//    env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/flinkCk"))
//    env.setStateBackend(new RocksDBStateBackend("file://"))
//
//    //启用检查点
//    env.enableCheckpointing(10 * 1000L)
//
//    //检查点配置信息
//    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
//    //    env.getCheckpointConfig.setCheckpointInterval(10 * 1000L)
//    env.getCheckpointConfig.setCheckpointTimeout(60 * 1000L)
//    env.getCheckpointConfig.setMaxConcurrentCheckpoints(3)
//    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(3)
//    env.getCheckpointConfig.setPreferCheckpointForRecovery(true)
//
//  }
//
//}
//
//class MyStateFunction extends RichMapFunction[String, String] with ListCheckpointed[Long] {
//
//  //1.值状态
//  lazy val valueState: ValueState[Long] = getRuntimeContext.getState(new ValueStateDescriptor[Long]("value-state", classOf[Long]))
//
//  //2.集合状态
//  lazy val listState: ListState[String] = getRuntimeContext.getListState(new ListStateDescriptor[String]("list-state", classOf[String]))
//
//  //3.Map状态
//  lazy val mapState: MapState[String, Double] = getRuntimeContext.getMapState(new MapStateDescriptor[String, Double]("map-state", classOf[String], classOf[Double]))
//
//  //4.Reduce状态
//  lazy val reduceState: ReducingState[String] = getRuntimeContext.getReducingState(new ReducingStateDescriptor[String]("reduce-state", new MyStateReduce, classOf[String]))
//
//  var count: Long = 0L
//
//  override def map(value: String): String = {
//
//    //1.值状态的使用
//    valueState.value()
//    valueState.update(0L)
//    valueState.clear()
//
//    //2.集合状态的使用
//    listState.add("")
//    listState.update(new util.ArrayList[String]())
//    listState.addAll(new util.ArrayList[String]())
//    listState.clear()
//
//    //3.Map状态
//    mapState.put("", 0.0)
//    mapState.values()
//    mapState.isEmpty
//    mapState.putAll(new util.HashMap[String, Double]())
//    mapState.remove("")
//    mapState.clear()
//
//    //4.Reduce状态
//    reduceState.add("")
//    reduceState.get()
//    reduceState.clear()
//
//    ""
//  }
//
//  override def restoreState(state: util.List[Long]): Unit = {
//    val iter: util.Iterator[Long] = state.iterator()
//    while (iter.hasNext) {
//      count += iter.next()
//    }
//  }
//
//  override def snapshotState(checkpointId: Long, timestamp: Long): util.List[Long] = {
//    val list = new util.ArrayList[Long]()
//    list.add(count)
//    list
//  }
//}
//
//class MyStateReduce extends ReduceFunction[String] {
//  override def reduce(value1: String, value2: String): String = {
//    value1 + value2
//    Math.max(value1.length, value2.length).toString
//  }
//}