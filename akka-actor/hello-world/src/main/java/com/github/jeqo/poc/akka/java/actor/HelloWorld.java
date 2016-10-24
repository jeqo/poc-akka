package com.github.jeqo.poc.akka.java.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author jeqo
 */
class HelloWorld extends UntypedActor {

    @Override
    public void preStart() throws Exception {
        final ActorRef greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
        greeter.tell(Greeter.Message.GREET, getSelf());
    }

    public void onReceive(Object message) throws Throwable {
        if (message == Greeter.Message.DONE) {
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
