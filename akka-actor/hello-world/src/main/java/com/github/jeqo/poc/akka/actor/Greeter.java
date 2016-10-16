package com.github.jeqo.poc.akka.actor;

import akka.actor.UntypedActor;

/**
 * @author jeqo
 */
class Greeter extends UntypedActor {

    enum Message {
        GREET, DONE
    }

    public void onReceive(Object message) throws Throwable {
        if (message == Message.GREET) {
            System.out.println("Hello World!");
            getSender().tell(Message.DONE, getSelf());
        } else {
            unhandled(message);
        }
    }
}
