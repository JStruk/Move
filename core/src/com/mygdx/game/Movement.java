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

public class Movement extends ApplicationAdapter implements InputProcessor, GestureDetector.GestureListener {
    private World world;
    private Box2DDebugRenderer b2dr;
    int nNum;
    public static final float fPpm = 100;
    public static final short shGround = 2, shPlayer = 4;
    public static final int nWidth = 320, nHeight = 240;
    private OrthographicCamera b2dCam;
    private CollisionDetector collisionDetector;
    GestureDetector gestureDetector;
    InputProcessor inputProcessor;
    private Body playerBody;
    InputMultiplexer multiPlexer;
    // GestureDetector gestureDetector;
    float w, h;
    Vector2 vPlat1, vPlat2;
    Sprite sDude, sPlat;
    SpriteBatch batch;

    Array<Body> arBodies = new Array<Body>();

    public void create() {
        gestureDetector = new GestureDetector(this);
     //   inputProcessor = new InputProcessor(this);
        InputMultiplexer multi = new InputMultiplexer();
        multi.addProcessor(this);
        multi.addProcessor(gestureDetector);
        batch = new SpriteBatch();
        collisionDetector = new CollisionDetector();
        vPlat1 = new Vector2(-15, 0);
        vPlat2 = new Vector2(15, 0);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        //  gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(multi);


        world = new World(new Vector2(0, -9.81f), true);

        world.setContactListener(collisionDetector);

        b2dr = new Box2DDebugRenderer();

        // create platform
        BodyDef bdef = new BodyDef();
        //  bdef.position.set(160 / fPpm, 120 / fPpm);7
        bdef.position.set(160 / fPpm, 10 / fPpm);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / fPpm, 5 / fPpm);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = shGround;
        fdef.filter.maskBits = shPlayer;

        body.createFixture(fdef).setUserData(Gdx.files.internal("platform.png"));
        sPlat = new Sprite(new Texture(Gdx.files.internal("platform.png")));
        sPlat.setSize((28 / fPpm) * 4, (30 / fPpm) * 4);

        sPlat.setOrigin(sPlat.getWidth() / 2, sPlat.getHeight() / 2);
        body.setUserData(sPlat);
        //  FixtureDef ground = new FixtureDef();


        // create player
        bdef.position.set(160 / fPpm, 200 / fPpm);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / fPpm, 5 / fPpm);
        fdef.shape = shape;
        fdef.filter.categoryBits = shPlayer;
        fdef.filter.maskBits = shGround;
        playerBody.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(2 / fPpm, 2 / fPpm, new Vector2(0, -5 / fPpm), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = shPlayer;
        fdef.filter.maskBits = shGround;
        fdef.isSensor = true;
        playerBody.createFixture(fdef).setUserData("foot");
        sDude = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        sDude.setSize((2 / fPpm) * 4, (2 / fPpm) * 4);
        sDude.setOrigin(sDude.getWidth() / 2, sDude.getHeight() / 2);
        playerBody.setUserData(sDude);


        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, nWidth / fPpm, nHeight / fPpm);

    }

    public void update(float dt) {
        dt = Game.STEP;
        world.step(dt, 6, 2);
    }

    public void render() {
        //  playerBody.setLinearVelocity(0f, -5f);
        //  world.step();
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(Game.STEP);


        // draw box2d world
        b2dr.render(world, b2dCam.combined);
        batch.setProjectionMatrix(b2dCam.combined);
        batch.begin();
        world.getBodies(arBodies);
        for (Body body : arBodies)
            if (body.getUserData() != null && body.getUserData() instanceof Sprite) {
                Sprite sprite = (Sprite) body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(batch);
            }
        batch.end();
    }

    public void dispose() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            playerBody.setLinearVelocity(-1f, 0f);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            playerBody.setLinearVelocity(1f, 0f);
        }
        // if (collisionDetector.hitTest()) {
        //   if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
        //   playerBody.applyForceToCenter(0, 200, true);
        //  }
        //}

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
    public boolean tap(float x, float y, int count, int button) {
      if(collisionDetector.hitTest()){
            System.out.println("tap");
            playerBody.applyForceToCenter(0, 200, true);
        }
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









