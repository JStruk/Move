package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/*
 * Created by kesty on 2015-04-30.
 */
public class kirby {
    final float fPpm = 100;
    Animation rightAnim, leftAnim, idleAnim;
    Sprite rightMove[] = new Sprite[10];
    Sprite leftMove[] = new Sprite[10];
    Sprite idleR[] = new Sprite[122];
    Sprite idleL[] = new Sprite[122];
    TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("kirby/kirbyidle.pack"));

    public kirby(TextureAtlas taKirby) {
        for (int i = 0; i < 10; i++) {
            rightMove[i] = new Sprite(taKirby.findRegion("frame-" + (i + 1)));
            rightMove[i].setSize((114 / fPpm) / 5, (105 / fPpm) / 5);
            leftMove[i] = new Sprite(taKirby.findRegion("frame-" + (i + 1)));
            leftMove[i].setSize((114 / fPpm) / 5, (105 / fPpm) / 5);
            leftMove[i].setFlip(true, false);
        }
        for (int i = 0; i < 122; i++) {
            idleR[i] = new Sprite(taIdle.findRegion(("frame (") + (i + 1) + (")")));
            idleR[i].setSize((30 / fPpm) / 2, (27 / fPpm) / 2);
            idleL[i] = new Sprite(taIdle.findRegion(("frame (") + (i + 1) + (")")));
            idleL[i].setSize((30 / fPpm) / 2, (27 / fPpm) / 2);
            idleL[i].setFlip(true, false);
        }
        idleAnim = new Animation(10f, idleR);
        rightAnim = new Animation(10f, rightMove);
        leftAnim = new Animation(10f, leftMove);
    }
}
