package scenes;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.krishna.birdtwo.GameMain;

import bird.Bird;
import ground.GroundBody;
import helpers.GameInfo;
import hud.UIHud;
import pipes.Pipes;

public class Gameplay implements Screen, ContactListener {
    //variable
    //Background variables
    private GameMain game;
    private Array<Sprite> bgs = new Array<Sprite>();

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    //for shapes like circle,
    private OrthographicCamera debugCamera;
    private Box2DDebugRenderer debugRenderer;

    private Bird bird; //linking bird class as variable.
    private World world;

    private UIHud hud;
   // private boolean firstTouch;

    private Array<Sprite> grounds = new Array<Sprite>(); // grounds sprite array
    //ground linking
    private GroundBody groundBody;


    // Note inside Array it is not sprite it is custom class Pipes
    private Array<Pipes> pipesArray = new Array<Pipes>();
    private final int DISTANCE_BETWEEN_PIPES = 120;




    //constructor
    public Gameplay(GameMain game) { //initilization of arguments is important.
        this.game = game;
        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2,0); //camera center

        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);
        createBackgrounds();
        createGrounds();

        //for creating visible circle shape
        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM,
            GameInfo.HEIGHT / GameInfo.PPM); //false sets coordinates at 0,0
        debugCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT, 0);

        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        //accessing bird custom class
        world = new World(new Vector2(0, -9.8f),true); //9.8 gravity control

        //after implementing contact listener use this
        world.setContactListener(this);


        bird = new Bird(world, GameInfo.WIDTH / 2f - 100, GameInfo.HEIGHT/2f);
        // 2f - 100 by changing 100 to 200 bird moves left hand side further.

        //ground custom class linking.
        groundBody = new GroundBody(world, grounds.get(0)); // no falling of bird.
        // grounds.get(0) it automatically prevents bird from falling.

        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                // put custom code
                createPipes();

            }
        });

        // For every two seconds new pipes will spawn continiously - infinite
        SequenceAction sa = new SequenceAction();
        sa.addAction(Actions.delay(3f));
        sa.addAction(run);

        hud.getStage().addAction( Actions.forever(sa));






    } // constructor

    //custom methods

    void rkUpdate(float dt) {
//        moveBackgrounds();
//        moveGrounds();
//        birdJump();
//        updatePipes();

        //for stopping background moving when bird hits ground or pipe
       // checkForFirstTouch();
        if(bird.getAlive()) {
            moveBackgrounds();
            moveGrounds();
            birdJump();
            updatePipes();
            movePipes();

        }
    }

    void birdJump() { // same method name used in Bird.java
        if(Gdx.input.justTouched()) {
            bird.birdFlapJump();
        }
    }

//    void checkForFirstTouch() {
//        if(!firstTouch) {
//
//            if(Gdx.input.justTouched()) {
//                firstTouch = true;
//                bird.activateBird();
//            }
//            //firstTouch = true;
//            //bird.activateBird();
//        }
//
//    }

    //to create 3 background by using background image and sprite.
    void createBackgrounds() {
        for(int i = 0; i < 3; i++) {
            Sprite bg = new Sprite(new Texture("Backgrounds/Day.jpg"));
            bg.setPosition(i * bg.getWidth(),0);
            bgs.add(bg);
        }
    }

    void createGrounds(){
        for(int i = 0; i < 3; i++) {
            Sprite ground = new Sprite(new Texture("Backgrounds/Ground.png"));
            // ground.setPosition(i * ground.getWidth(),0); it places ground almost center

            // we can move groud by adjusting - 55 or something.
            ground.setPosition(i * ground.getWidth(), - ground.getHeight() / 2 - 55);
            grounds.add(ground);
        }
    }

    // draw backgrounds and render it, without render background is not visible.
    //sprite batch is used as argument and we call sprite batch from GameMain.java
    void drawBackgrounds(SpriteBatch batch) {
        for(Sprite s : bgs) { // Array for looping style. sprite with background images.
            batch.draw(s, s.getX(),s.getY());
        }
    }

    void drawGrounds(SpriteBatch batch){
        for(Sprite ground : grounds) {
            batch.draw(ground, ground.getX(), ground.getY());
        }

    }

    void createPipes() {
        Pipes p = new Pipes(world, GameInfo.WIDTH + DISTANCE_BETWEEN_PIPES);
        p.setMainCamera(mainCamera);
        pipesArray.add(p);
    }

    void drawPipes(SpriteBatch batch) {
        for(Pipes pipe : pipesArray) {
            pipe.drawPipes(batch);
        }
    }

    void updatePipes() {
        for(Pipes pipe : pipesArray) {
            pipe.updatePipes();
        }
    }

    void movePipes() {
        for(Pipes pipe : pipesArray) {
            pipe.movePipes();
        }
    }

    // method for collision and remove the bird

    void stopPipes() {
        for(Pipes pipe : pipesArray) {
            pipe.stopPipes();
        }
    }

    void birdDied() {
        bird.setAlive(false);
        stopPipes();
        hud.getStage().clear();
    }


    //now background images are created, drawn and now to move three images.
    void moveBackgrounds() {
        for(Sprite bg : bgs) {
            float x1 = bg.getX() - 2f; // left moving backgrounds use minus sign
            bg.setPosition(x1, bg.getY());

            //infinite looping - important - maths calculation
            if(bg.getX() + GameInfo.WIDTH + (bg.getWidth() / 2f ) < mainCamera.position.x) {
                float x2 =  bg.getX() + bg.getWidth() * 3;
                bg.setPosition(x2,bg.getY());
            }

        }
    }

    void moveGrounds(){
        for(Sprite ground:grounds){
            float x1 = ground.getX() - 1f;
            ground.setPosition(x1,ground.getY());
            //infinite looping
            if(ground.getX() + GameInfo.WIDTH + (ground.getWidth() / 2f ) < mainCamera.position.x) {
                float x2 =  ground.getX() + ground.getWidth() * 3;
                ground.setPosition(x2,ground.getY());
          }
        }
    }

    //screen abstract default methods
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        rkUpdate(delta); // for moving backgrounds and grounds. dt - delta time
        //rkUpdate(delta) argument delta comes from render(float delta).

        game.getBatch().begin();
        drawBackgrounds(game.getBatch());
        drawGrounds(game.getBatch());
        bird.drawIdle(game.getBatch());

        drawPipes(game.getBatch());

        game.getBatch().end();

        //for drawing shape body
        debugRenderer.render(world,debugCamera.combined);

        //game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);

        hud.getStage().draw();
        hud.getStage().act();





        //bird linking
        bird.updateBird();

       // movePipes(); used in rkUpdate to stop moving bodies during collision.

        //very important lines for android phone display.
        //game.getBatch().setProjectionMatrix(mainCamera.combined);
        //mainCamera.update();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    //Contactlistenr abstract methods or default methods.
    //COLLISION METHODS DEFAULT FROM contact listener.

    @Override
    public void beginContact(Contact contact) {

        Fixture body1, body2; // body1 is the bird.

        if(contact.getFixtureA().getUserData() == "Bird") {
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        } else {
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Pipe") {
            //System.out.println("Kill - pipe");
            if(bird.getAlive()) {
                System.out.println("bird die");
                birdDied();
            }
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Ground") {
            //System.out.println("Kill - ground");
            if(bird.getAlive()) {
                System.out.println("bird die");
                birdDied();
            }
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Score") {
            //System.out.println("Kill - score");
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }




}// game play
