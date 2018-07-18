package poc;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import poc.avro.LogLine;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class App {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();
    ActorMaterializer mat = ActorMaterializer.create(system);

    ProducerSettings<Object, Object> settings =
        ProducerSettings.create(system, Optional.empty(), Optional.empty())
            .withBootstrapServers("localhost:9092");

    Sink<ProducerRecord<Object, Object>, CompletionStage<Done>> sink = Producer.plainSink(settings);

    Source
        .single(
            LogLine.newBuilder()
                .setIp("123.123.123.123")
                .setReferrer("someone")
                .setSessionid(1)
                .setTimestamp(new Date().getTime())
                .setUrl("http://google.com")
                .setUseragent("jeqo")
                .build())
        .map(l -> new ProducerRecord<Object, Object>("test", 1, l))
        .runWith(sink, mat);
  }
}
