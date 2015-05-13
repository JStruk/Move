package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by kesty on 2015-04-30.
 */
public class kirby {
    final float fPpm = 100;
    Animation rightAnim, leftAnim;
    Sprite rightMove[] = new Sprite[10];
    Sprite leftMove[] = new Sprite[10];

    public kirby(TextureAtlas taKirby) {
        for (int i = 0; i < 10; i++) {
            rightMove[i] = new Sprite(taKirby.findRegion("frame-" + (i + 1)));
            //rightMove[i].setSize((2 / fPpm) * 12, (2 / fPpm) * 12);
            rightMove[i].setOrigin(rightMove[i].getWidth(), rightMove[i].getHeight());
            rightMove[i].setSize((114 / fPpm) / 5, (105 / fPpm) / 5);
            leftMove[i] = new Sprite(taKirby.findRegion("frame-" + (i + 1)));
            leftMove[i].setSize((114 / fPpm) / 5, (105 / fPpm) / 5);
            leftMove[i].setOrigin(rightMove[i].getWidth(), rightMove[i].getHeight());
            leftMove[i].setFlip(true, false);
        }
        rightAnim = new Animation(0.1f, rightMove);
        leftAnim = new Animation(0.1f, leftMove);
    }
}
