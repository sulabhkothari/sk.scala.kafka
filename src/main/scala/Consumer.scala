import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.{Calendar, Properties}

import scala.collection.JavaConverters._
object Consumer {
  def main(args: Array[String]): Unit = {
    consumeFromKafka("test")
  }
  def consumeFromKafka(topic: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "earliest")
    props.put("group.id", "consumer-group-" + Calendar.getInstance().getTimeInMillis)
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(topic))

    while (true) {
      println("*************************")

      val record = consumer.poll(10000).asScala
      for (data <- record.iterator)
        println(data.value())
    }
  }
}
