package com.jpm.test.s3app.testutils;

public class ThrowableCaptor {

    @FunctionalInterface
    public interface Actor {
        void act() throws Throwable;
    }

    public static Throwable thrownBy(Actor actor) {
        try {
            actor.act();
        } catch( Throwable throwable ) {
            return throwable;
        }
        return null;
    }
}
