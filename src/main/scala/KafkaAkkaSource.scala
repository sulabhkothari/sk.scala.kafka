import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Attributes, Materializer, Outlet, SourceShape}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.stage.{AbstractOutHandler, GraphStage, GraphStageLogic, OutHandler}

class KafkaAkkaSource extends GraphStage[SourceShape[Int]] {
  val out = Outlet.create[Int]("RandomNumberSource.out")
  val shape1 = SourceShape.of(out)

  override def shape = shape1

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      // All state MUST be inside the GraphStageLogic,
      // never inside the enclosing GraphStage.
      // This state is safe to access and modify from all the
      // callbacks that are provided by GraphStageLogic and the
      // registered handlers.
      private var counter:Int = 1

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          push(out, counter)
          counter += 1
        }
      })
    }
}


object KafkaAkkaSource extends App{
  val system = ActorSystem.create("StreamsExamples")
  implicit val mat = ActorMaterializer.create(system)


  val numbers = Source.fromGraph(new KafkaAkkaSource).mapMaterializedValue((o) => NotUsed.getInstance)

  val runnable = numbers.take(10).to(Sink.foreach(println))

  // we can materialize the same stream multiple times:
  runnable.run
  runnable.run
  runnable.run
}
