package com.github.jeqo.poc.akka.actor;

/**
 * @author jeqo
 */
public class Main {

    public static void main(String... args) {
        akka.Main.main(new String[]{HelloWorld.class.getName()});
    }
}
