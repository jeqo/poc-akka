package com.github.jeqo.poc.akka.java.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by jeqo on 24.10.16.
 */
public class ProjectManagerActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Using the ActorSystem will create top-level actors, supervised by the actor system’s provided guardian actor,
     * while using an actor’s context will create a child actor
     */
    final ActorRef backendDeveloper = getContext().actorOf(Props.create(DeveloperActor.class, 3), "backend");
    final ActorRef frontendDeveloper = getContext().actorOf(Props.create(DeveloperActor.class, 2), "frontend");
    final ActorRef devopsDeveloper = getContext().actorOf(Props.create(DeveloperActor.class, 1), "devops");

    public static class ProjectMessage {
        private final String name;

        public ProjectMessage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void onReceive(Object message) throws Throwable {
        if (message instanceof ProjectMessage) {
            final ProjectMessage projectMessage = (ProjectMessage) message;
            log.info("Received Project message {}", projectMessage.getName());
            backendDeveloper.tell(new DeveloperActor.TaskMessage(projectMessage.getName(), "integration poc"), getSelf());
            frontendDeveloper.tell(new DeveloperActor.TaskMessage(projectMessage.getName(), "design ui"), getSelf());
            devopsDeveloper.tell(new DeveloperActor.TaskMessage(projectMessage.getName(), "prepare dev env"), getSelf());
            getSender().tell("project prepared", getSelf());
        } else unhandled(message);
    }

    public static void main(String... args) {
        final ActorSystem system = ActorSystem.create("MySystem");
        final ActorRef myActor = system.actorOf(Props.create(ProjectManagerActor.class), "pm");
        myActor.tell(new ProjectMessage("P1"), null);
    }
}
