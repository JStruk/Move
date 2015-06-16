package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Justin on 2015-04-07.
 */
public class CollisionDetector implements ContactListener {
    private static boolean isHit;
    public int nScore = 0;

    public void beginContact(Contact c) {
        nScore++;
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();
        isHit = true;
        if (fa.getUserData() != null && fa.getUserData().equals("player")) {
            isHit = true;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("player")) {
            isHit = true;
        }
    }

    public void endContact(Contact c) {

        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa.getUserData() != null && fa.getUserData().equals("player")) {
            isHit = false;
            System.out.println("false");
        }
        if (fb.getUserData() != null && fb.getUserData().equals("player")) {
            isHit = false;
        }
    }

    public static boolean hitTest() {
        return isHit;
    }

    public int Score() {
        return nScore;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
