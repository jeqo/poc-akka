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

    /**
     * When writing code outside of actors which shall communicate with actors, the ask pattern can be a solution
     * (see below), but there are two things it cannot do: receiving multiple replies (e.g. by subscribing an
     * ActorRef to a notification service) and watching other actors’ lifecycle. For these purposes there is the
     * Inbox class
     * @param args
     */
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
