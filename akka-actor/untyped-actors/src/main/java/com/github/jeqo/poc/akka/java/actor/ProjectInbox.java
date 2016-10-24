package com.github.jeqo.poc.akka.java.actor;

import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by jeqo on 24.10.16.
 */
public class ProjectInbox {


    public static void main(String... args){
        final ActorSystem system = ActorSystem.create("MySystem");
        final Inbox inbox = Inbox.create(system);
        inbox.send(system.actorOf(Props.create(ProjectManagerActor.class), "pm"), new ProjectManagerActor.ProjectMessage("hello"));
        try {
            String response = (String)inbox.receive(Duration.create(1, TimeUnit.SECONDS));
            assert response.equals("project prepared");
        } catch (java.util.concurrent.TimeoutException e) {
            // timeout
        }
    }
}
