package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Justin on 2015-04-07.
 */
public class CollisionDetector implements ContactListener {
    private static boolean isHit;

    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();
        isHit = true;
        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            isHit = true;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            isHit = true;
        }
    }

    public void endContact(Contact c) {

        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            isHit = false;
            System.out.println("false");
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            isHit = false;
        }

    }

    public static boolean hitTest() {

        return isHit;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
