package com.github.jeqo.poc.akka.java.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

/**
 * @author jeqo
 */
public class DeveloperActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final int velocity;

    DeveloperActor(int velocity) {
        this.velocity = velocity;
    }

    public static Props props(final int velocity) {
        return Props.create(new Creator<DeveloperActor>() {
            public DeveloperActor create() throws Exception {
                return new DeveloperActor(velocity);
            }
        });
    }

    public static class TaskMessage {
        private final String project;
        private final String taskName;

        public TaskMessage(String project, String taskName) {
            this.project = project;
            this.taskName = taskName;
        }

        public String getProject() {
            return project;
        }

        public String getTaskName() {
            return taskName;
        }
    }

    public void onReceive(Object message) throws Throwable {
        if (message instanceof TaskMessage) {
            final TaskMessage taskMessage = (TaskMessage) message;
            log.info("Received Task message {}=>{}", taskMessage.getProject(), taskMessage.getTaskName());
            getSender().tell(message, getSelf());
        } else unhandled(message);
    }

    public static void main(String... args) {
        final ActorSystem system = ActorSystem.create("MySystem");
        final ActorRef myActor = system.actorOf(DeveloperActor.props(10), "dev1");
        myActor.tell(new TaskMessage("P1", "Impl Microservice"), null);
    }
}
