import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Attributes, Materializer, Outlet, SourceShape}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.stage.{AbstractOutHandler, GraphStage, GraphStageLogic, OutHandler}

import scala.collection.mutable

class KafkaAkkaSource extends GraphStage[SourceShape[String]] {
  val out = Outlet.create[String]("KafkaSource.out")
  val shape1 = SourceShape.of(out)

  override def shape = shape1
  val consumer = Consumer.consumeFromKafka(mutable.Queue.empty).iterator

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      // All state MUST be inside the GraphStageLogic,
      // never inside the enclosing GraphStage.
      // This state is safe to access and modify from all the
      // callbacks that are provided by GraphStageLogic and the
      // registered handlers.

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          push(out, consumer.next())
        }
      })
    }
}


object KafkaAkkaSource extends App{

  val system = ActorSystem.create("StreamsExamples")
  implicit val mat = ActorMaterializer.create(system)

  val numbers = Source.fromGraph(new KafkaAkkaSource).mapMaterializedValue((o) => NotUsed.getInstance)

  val runnable = numbers.to(Sink.foreach(println))

  // we can materialize the same stream multiple times:
  runnable.run
}
