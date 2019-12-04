import KafkaAkkaSink.system
import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.collection.parallel.immutable
import scala.concurrent.Future

object AlpakkaKafka extends App {
  val system = ActorSystem.create("StreamsExamples")

  val config = system.settings.config.getConfig("akka.kafka.producer")
  implicit val mat = ActorMaterializer.create(system)

  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")

  val topic = "test9999"
  val multi: ProducerMessage.Envelope[String, String, Int] =
    ProducerMessage.multi(
      scala.collection.immutable.Seq(
        new ProducerRecord("topicName", "", ""),
        new ProducerRecord("anotherTopic", "", "")
      ),
      0
    )

  val done: Future[Done] =
  Source(11 to 12)
      .map(_.toString)
      .map(value => new ProducerRecord[String, String](topic, value))
      .runWith(Producer.plainSink(producerSettings))

}
