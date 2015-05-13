package com.mygdx.game;

/**
 * Created by Justin on 2015-05-01.//
 */
public class HitTest {
    private float fX, fY, fW, fH, w, fPPM;
    boolean isHitRight = false;
    boolean isHitLeft = false;
    int nCount;

    public HitTest(float _nPlayerX, float _nPlayerY, float _nPlayerWidth, float _nPlayerHeight, float _nWidth, float _fPPM) {
        fX = _nPlayerX;
        fY = _nPlayerY;
        fW = _nPlayerWidth;
        fH = _nPlayerHeight;
        w = _nWidth;
        fPPM = _fPPM;
        WallTestRight();
        WallTestLeft();
        nCount++;
        if (nCount % 30 == 0) {
            System.out.println("player x: " + fX + " wall left "+(0+fW)+" wall right: " + w);
        }
    }

    public void WallTestRight() {
        isHitRight = false;
        if (fX >= (w - fW)) {
            isHitRight = true;
        } else {
            isHitRight = false;
        }
    }

    public void WallTestLeft() {
        isHitLeft = false;
        if (fX <= (0 - fW)) {
            isHitLeft = true;
        } else {
            isHitLeft = false;
        }
    }

    public boolean isHitRight() {
        return isHitRight;
    }

    public boolean isHitLeft() {
        return isHitLeft;
    }

}
