import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class App {


  public static void main(String[] args) {

    final ActorSystem actorSystem = ActorSystem.create("flow-monitor-test");
    final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);


    Flow<String, String, NotUsed> flow = Flow.of(String.class).map(a -> a + " world").monitor((a, b) -> {
      System.out.println(b.state());
      return a;
    });


    Source.repeat("hello")
        .take(10)
        .via(flow)
        .runWith(Sink.foreach(System.out::println), materializer);
  }
}
