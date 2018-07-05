package io.github.jeqo.poc.akka.streams.opencensus;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;

import static java.lang.System.out;

public class App {
  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create("a");
    ActorMaterializer mat = ActorMaterializer.create(system);

    LoggingTraceExporter.register();

    Flow<String, String, NotUsed> flow =
        Flow.of(String.class)
            .map(s -> s + " world");
    Source.repeat("hello")
        .take(1)
        .async()
        .via(OpenCensus.spanFlow(flow))
//        .via(flow)
        .async()
        .runWith(Sink.foreach(out::println), mat);
  }
}
