import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.{Calendar, Properties}

import scala.collection.JavaConverters._
import scala.collection.immutable.Queue
import scala.collection.mutable

object Consumer {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "earliest")
  val cgn = "consumer-group-11" + Calendar.getInstance().getTimeInMillis
  println(cgn)
  props.put("group.id", cgn)
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("test9999"))
  //
  //  for {
  //    str <- consumeFromKafka(mutable.Queue.empty)
  //  } println(str)

  def consumeFromKafka(queue: mutable.Queue[String]): Stream[String] = {
    println("*****************")

    if (!queue.isEmpty) {
      val v = queue.dequeue()
      return v #:: consumeFromKafka(queue)
    }

    val record = consumer.poll(1000).asScala
    record.iterator.map(_.value()).toList match {
      case List() => consumeFromKafka(queue)
      case x :: List() => x #:: consumeFromKafka(queue)
      case y :: tail =>
        queue.enqueue(tail: _*)
        y #:: consumeFromKafka(queue)
    }
  }
}
