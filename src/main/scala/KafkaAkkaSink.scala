import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Attributes, Inlet, SinkShape}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.stage.GraphStage
import akka.stream.stage.GraphStageLogic
import akka.stream.stage.InHandler

class KafkaAkkaSink extends GraphStage[SinkShape[String]] {
  val in: Inlet[String] = Inlet("KafkaSink.in")
  override val shape: SinkShape[String] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      // This requests one element at the Sink startup.
      override def preStart(): Unit = pull(in)

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          Thread.sleep(1000)
          val msg = grab(in)
          if (msg != "") {
            KafkaProducer.writeToKafka("test9999", msg)
            println("Perpetual msg --> " + msg)
          }
          pull(in)
        }
      })
    }
}

object KafkaAkkaSink extends App {

  val system = ActorSystem.create("StreamsExamples")
  implicit val mat = ActorMaterializer.create(system)

  val numbers = Source.fromGraph(new KafkaAkkaSource).mapMaterializedValue((o) => NotUsed.getInstance)

  val runnable = numbers.to(new KafkaAkkaSink)

  // we can materialize the same stream multiple times:
  runnable.run
}
