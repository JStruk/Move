package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Movement extends ApplicationAdapter implements InputProcessor, GestureDetector.GestureListener {
    public static final float fPpm = 100;
    public static final short shGround = 2, shPlayer = 4;
    public static final int nWidth = 320, nHeight = 240;
    public static final float STEP = 1 / 60f;
    float w, h;
    Vector2 vPlat1, vPlat2;
    Sprite sDude, sPlat;
    SpriteBatch batch;
    TextureAtlas taKirby;
    Texture tPlat;
    kirby kirby;
    float elapsedTime;
    int i = 0, j = 0;
    boolean bL, bR, bFL, bFR;
    Array<Body> arBodies = new Array<Body>();
    Music mp3Sound;
    private World world;
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera camera;
    private CollisionDetector collisionDetector;
    private Body playerBody;

    public void runAudio() {
        mp3Sound = Gdx.audio.newMusic(Gdx.files.internal("mayro.mp3"));
        //mp3Sound.setVolume(0.5f);
        mp3Sound.play();
        //audio must be 44k 128hz mono... audiocache will overflow otherwise
    }

    public void create() {
        runAudio();

        // Discovered "InputMultiplexer" here!: http://www.badlogicgames.com/forum/viewtopic.php?f=20&t=10690
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(this);
        multi.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(multi);

        taKirby = new TextureAtlas(Gdx.files.internal("kirby.pack"));

        kirby = new kirby(taKirby);

        batch = new SpriteBatch();
        collisionDetector = new CollisionDetector();
        vPlat1 = new Vector2(-15, 0);
        vPlat2 = new Vector2(15, 0);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(collisionDetector);

        b2dr = new Box2DDebugRenderer();

       /* // create platform
        BodyDef bdef = new BodyDef();
        bdef.position.set(160 / fPpm, 10 / fPpm);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / fPpm, 5 / fPpm);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = shGround;
        fdef.filter.maskBits = shPlayer;

        body.createFixture(fdef);

        tPlat = new Texture("platform.png");
        sPlat = new Sprite(tPlat);
        //sPlat = new Sprite(new Texture(Gdx.files.internal("platform.png"))); //Create sprite for the platform
        sPlat.setSize((28 / fPpm) * 5, (30 / fPpm) * 4);  // Makes the sprite the right size to just cover the Box2D Body
        //sPlat.setOrigin(sPlat.getWidth() / 2, sPlat.getHeight() / 2); // Sets the Origin of the sprite to the middle instead of the bottom left
        sPlat.setPosition(body.getPosition().x, body.getPosition().y);
        body.setUserData(sPlat); // set the user data as the sprite so in render it returns as an instance of a sprite to draw the sprite on the body :D
        //https://www.youtube.com/watch?v=1cB-iWycUH4
        //This video explains very well how to associate the sprite with the body and what is really happening when drawing a sprite and a body together, thanks :)
*/
        tPlat = new Texture("platform.png");
        sPlat = new Sprite(tPlat);
        sPlat.setSize((28 / fPpm) * 5, (30 / fPpm) * 4);
        sPlat.setPosition(160 / fPpm, 10 / fPpm);
        BodyDef bdef = new BodyDef();

        bdef.position.set(sPlat.getX(), sPlat.getY());
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / fPpm, 5 / fPpm);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = shGround;
        fdef.filter.maskBits = shPlayer;

        body.createFixture(fdef);
        body.setUserData(sPlat);
        // create player
        bdef.position.set(160 / fPpm, 200 / fPpm);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(7 / fPpm, 7 / fPpm);
        fdef.shape = shape;
        fdef.filter.categoryBits = shPlayer;
        fdef.filter.maskBits = shGround;
        playerBody.createFixture(fdef);

        //playerBody.setUserData(kirby.idleR[j]);
        playerBody.setUserData(kirby.idleR[j]);

        playerBody.createFixture(fdef).setUserData("player"); //Set the user data of the playerbody to a string to check in the collision detector
        /*sDude = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg"))); //Create the sprite for the player
        sDude.setSize((2 / fPpm) * 4, (2 / fPpm) * 4); // set the size of the player to the same size as the body (Box2s uses metres, sprites use pixels so weird conversions, this one looked okay)
        sDude.setOrigin(sDude.getWidth() / 2, sDude.getHeight() / 2);  //set the origin of the sprite to the middle instead of bottom left*/

        //playerBody.setUserData(sDude); //set the user data of the player as the sprite so it returns as an instance of a sprite to draw sprite on the body

        // set up box2d cam
        camera = new OrthographicCamera();
        camera.setToOrtho(false, nWidth / fPpm, nHeight / fPpm);
    }

    public void render() {
        if (i == 9) {
            i = 0;
        }
        if (j == 121) {
            j = 0;
        }
        i++;
        if (bR) {
            playerBody.setLinearVelocity(1f, playerBody.getLinearVelocity().y);
            // do not put y to 0... otherwise all upward momentum will be lost if moving in mid air
            playerBody.setUserData(kirby.rightMove[i]);
        } else if (bL) {
            playerBody.setLinearVelocity(-1f, playerBody.getLinearVelocity().y);
            playerBody.setUserData(kirby.leftMove[i]);
        } else if (!bR && bFR) {
            playerBody.setUserData(kirby.idleR[j]);
            j++;
        } else if (!bL && bFL) {
            playerBody.setUserData(kirby.idleL[j]);
            j++;
        }
        elapsedTime += Gdx.graphics.getDeltaTime();
        //update camera to player location
        camera.position.set(camera.position.x, playerBody.getPosition().y, 0);
        camera.update();

        //apply the physics to/update the world every 1/60th of a second
        world.step(STEP, 6, 2);

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render b2d world
        b2dr.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        //start drawing the sprites
        batch.begin();
        //put all the bodies on the stage into an array of bodies
        world.getBodies(arBodies);
        for (Body body : arBodies)
            if (body.getUserData() != null && body.getUserData() instanceof Sprite) { // check if the user data of the body is a sprite, then grab that sprite and draw it on the bodys position and rotation
                Sprite sprite = (Sprite) body.getUserData();
                if (sprite == sPlat) {
                    sprite.setPosition((body.getPosition().x - (sprite.getWidth() * 2) / 5), body.getPosition().y - (((sprite.getHeight() * 2) / 3) - (7 / fPpm)));
                } else {
                    sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 4);
                }
                sprite.draw(batch);
            }
        batch.end();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (collisionDetector.hitTest()) {//if the player is on a platform, and the screen is tapped allow the player to jump
            //playerBody.applyForceToCenter(playerBody.getLinearVelocity().x, 200, true);//jump :D
            playerBody.applyLinearImpulse(playerBody.getLinearVelocity().x, 3, playerBody.getPosition().x, playerBody.getPosition().y, true);
            //forcetocenter uses pixels... linearimpulse uses meters
            //(as in the integers)
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            bFL = true;
            bL = true;
            bFR = false;
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            bFR = true;
            bR = true;
            bFL = false;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        bL = false;
        bR = false;
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }


    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}









