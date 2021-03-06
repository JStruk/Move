package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Movement extends ApplicationAdapter implements InputProcessor, GestureDetector.GestureListener, ApplicationListener {
    private World world;
    private Box2DDebugRenderer b2dr;
    public static final float fPpm = 100;
    public static final short shGround = 2, shPlayer = 4;
    public static final int nWidth = 320, nHeight = 240;
    private OrthographicCamera camera;
    private CollisionDetector collisionDetector;
    private HitTest hitTest;
    private Body playerBody;
    public static final float STEP = 1 / 60f;
    float w, h;
    int nCount = 0;
    int nAccelX, nAccelY;
    Vector2 vPlat1, vPlat2;
    Sprite sDude, sPlat;
    SpriteBatch batch;
    float acceleration;
    boolean isHitLeft=false, isHitRight=false;

    Array<Body> arBodies = new Array<Body>();

    public void create() {
        // Discovered "InputMultiplexer" here!: http://www.badlogicgames.com/forum/viewtopic.php?f=20&t=10690
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(this);
        multi.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(multi);

        batch = new SpriteBatch();
        collisionDetector = new CollisionDetector();
        vPlat1 = new Vector2(-15, 0);
        vPlat2 = new Vector2(15, 0);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(collisionDetector);

        b2dr = new Box2DDebugRenderer();

        //create right wall
        BodyDef rightwalldef = new BodyDef();
        rightwalldef.type = BodyDef.BodyType.StaticBody;
        rightwalldef.position.set((Gdx.graphics.getHeight() / 2) / fPpm, 0); // gameBed.position is the world co-ordinate of the bottom left corner of the rectangular block
        Body rightwallbody = world.createBody(rightwalldef);
        PolygonShape rightwall = new PolygonShape();
        //rightwalldef.origin.x = 0.005f;
        // origin.y = (float)(gameBed.bounds.getHeight()) / 2;
        rightwall.setAsBox(Gdx.graphics.getWidth() / fPpm, 0);
        FixtureDef rightwallFixtureDef = new FixtureDef();
        rightwallFixtureDef.shape = rightwall;
        rightwallFixtureDef.restitution = 0.9f;
        rightwallbody.createFixture(rightwallFixtureDef);
        rightwall.dispose();


        // create platform
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

        sPlat = new Sprite(new Texture(Gdx.files.internal("platform.png"))); //Create sprite for the platform
        sPlat.setSize((28 / fPpm) * 4, (30 / fPpm) * 4);  // Makes the sprite the right size to just cover the Box2D Body
        sPlat.setOrigin(sPlat.getWidth() / 2, sPlat.getHeight() / 2); // Sets the Origin of the sprite to the middle instead of the bottom left
        body.setUserData(sPlat); // set the user data as the sprite so in render it returns as an instance of a sprite to draw the sprite on the body :D
        //https://www.youtube.com/watch?v=1cB-iWycUH4
        //This video explains very well how to associate the sprite with the body and what is really happening when drawing a sprite and a body together, thanks :)

        // create player
        bdef.position.set(160 / fPpm, 200 / fPpm);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / fPpm, 5 / fPpm);
        fdef.shape = shape;
        fdef.filter.categoryBits = shPlayer;
        fdef.filter.maskBits = shGround;
        playerBody.createFixture(fdef);

        playerBody.createFixture(fdef).setUserData("player"); //Set the user data of the playerbody to a string to check in the collision detector
        sDude = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg"))); //Create the sprite for the player
        sDude.setSize((2 / fPpm) * 4, (2 / fPpm) * 4); // set the size of the player to the same size as the body (Box2s uses metres, sprites use pixels so weird conversions, this one looked okay)
        sDude.setOrigin(sDude.getWidth() / 2, sDude.getHeight() / 2);  //set the origin of the sprite to the middle instead of bottom left
        playerBody.setUserData(sDude); //set the user data of the player as the sprite so it returns as an instance of a sprite to draw sprite on the body


        // set up box2d cam
        camera = new OrthographicCamera();
        camera.setToOrtho(false, nWidth / fPpm, nHeight / fPpm);
        hitTest = new HitTest(playerBody.getPosition().x, playerBody.getPosition().y, 160 / fPpm, 200 / fPpm, w, fPpm);
    }

    public void render() {
        w = ((Gdx.graphics.getWidth()) / fPpm) / 2;
        hitTest = new HitTest(playerBody.getPosition().x, playerBody.getPosition().y, 5 / fPpm, 5 / fPpm, w, fPpm);
        hitTest = new HitTest(playerBody.getPosition().x, playerBody.getPosition().y, 5 / fPpm, 5 / fPpm, w, fPpm);
        acceleration = Gdx.input.getAccelerometerX();
        nCount += 1;
        isHitRight = hitTest.isHitRight();
        isHitLeft = hitTest.isHitLeft();

        if (hitTest.isHitRight()) {
            playerBody.setTransform((((Gdx.graphics.getWidth()) / fPpm) / 2)-(5/fPpm), playerBody.getPosition().y, playerBody.getAngle());
          //  playerBody.setLinearVelocity(0f,0f);
            System.out.println("true");
        }
        if (hitTest.isHitLeft()) {
            playerBody.setTransform(0+(5/fPpm), playerBody.getPosition().y, playerBody.getAngle());
            playerBody.setLinearVelocity(0f,0f);
            System.out.println("true");
        }
        //   hitTest.isHitRight = false;
        //  if (nCount % 60 == 0) {
        ///    System.out.println(acceleration);
        //}

        //  nAccelX = (int) Gdx.input.getAccelerometerX();
        //    nAccelY = (int) Gdx.input.getAccelerometerY();
        //  if(Math.abs(acceleration)>0.3f){
        //    playerBody.setLinearVelocity(1f, 0f);
        //}
        move(acceleration);
        //  System.out.println("accel x: " + Gdx.input.getAccelerometerY() + " accel y: " + Gdx.input.getAccelerometerY() + " orientation: " + Gdx.input.getNativeOrientation() + " Azimuth: " + Gdx.input.getAzimuth());
        //  if(Gdx.input.getAzimuth()<-170){
        //    playerBody.setLinearVelocity(1f, 0f);
        //}
        //update camera to player location
        camera.position.set(camera.position.x, playerBody.getPosition().y, 0);
        camera.update();
//
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
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(batch);
            }
        batch.end();
    }

    public void move(float _acceleration) {
        //    if (!hitTest.isCharacterHitWall()) {
        //      if (_acceleration > 0.3f) {
        //        playerBody.applyLinearImpulse(1f, 0f, playerBody.getPosition().x + (Gdx.graphics.getWidth() / 4), (playerBody.getPosition().y) + Gdx.graphics.getWidth() / 4, true);
        // playerBody.applyForce(1f, 0f, playerBody.getPosition().x + (Gdx.graphics.getWidth() / 4), (playerBody.getPosition().y) + Gdx.graphics.getWidth() / 4, true);
        //   }
        //  if (_acceleration < -0.3f) {
//          / playerBody.applyForce(-1f, 0f, playerBody.getPosition().x + (Gdx.graphics.getWidth() / 4), (playerBody.getPosition().y) + Gdx.graphics.getWidth() / 4, true);
        //   playerBody.applyLinearImpulse(-1f, 0f, playerBody.getPosition().x + (Gdx.graphics.getWidth() / 4), (playerBody.getPosition().y) + Gdx.graphics.getWidth() / 4, true);

        // }
//}

    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (collisionDetector.hitTest()) {//if the player is on a platform, and the screen is tapped allow the player to jump
            playerBody.applyForceToCenter(0, 200, true);//jump :D
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)&&!isHitLeft){
            playerBody.setLinearVelocity(-1f, 0f);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)&&!isHitRight) {
            playerBody.setLinearVelocity(1f, 0f);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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









