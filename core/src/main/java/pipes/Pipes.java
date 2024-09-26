package pipes;

//custom class which does not extend sprite.

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

import helpers.GameInfo;

public class Pipes {
    //variable declaration
    private World world;
    private Body body1, body2, body3;
    private Sprite pipe1, pipe2; // for drawing pipes sprite is used and can be manipulated.
    private final float DISTANCE_BETWEEN_PIPES = 420f;
    private Random random = new Random(); // this random comes from java.random.util
    private OrthographicCamera mainCamera;

    //constructor
    public Pipes(World world, float x) { //initilization is very important in constructor arguments
        this.world = world;
        createPipes(x, getRandomY());
    }

    void createPipes(float x, float y) {
        pipe1 = new Sprite(new Texture("Pipes/Pipe 1.png"));
        pipe2 = new Sprite(new Texture("Pipes/Pipe 2.png"));

        pipe1.setPosition(x,y + DISTANCE_BETWEEN_PIPES);
        pipe2.setPosition(x,y - DISTANCE_BETWEEN_PIPES);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody; // works ONLY on external force like linear impulse

        //creating body for pipe1 and pipe 2
        bodyDef.position.set(pipe1.getX() / GameInfo.PPM, pipe1.getY() / GameInfo.PPM);
        body1 = world.createBody(bodyDef);
        body1.setFixedRotation(false);

        bodyDef.position.set(pipe2.getX() / GameInfo.PPM, pipe2.getY() / GameInfo.PPM);
        body2 = world.createBody(bodyDef);
        body2.setFixedRotation(false);

        // creating body for score
        bodyDef.position.set(pipe1.getX() / GameInfo.PPM, y / GameInfo.PPM);
        body3 = world.createBody(bodyDef);
        body3.setFixedRotation(false);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox((pipe1.getWidth() / 2) / GameInfo.PPM,
            (pipe1.getHeight() / 2f) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //collide
        fixtureDef.filter.categoryBits = GameInfo.PIPE;

        Fixture fixture1 = body1.createFixture(fixtureDef);
        fixture1.setUserData("Pipe");
        Fixture fixture2 = body2.createFixture(fixtureDef);
        fixture2.setUserData("Pipe");

        shape.setAsBox(3 / GameInfo.PPM, (pipe1.getHeight() / 2f) / GameInfo.PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GameInfo.SCORE;
        fixtureDef.isSensor = true; // it allows bird to pass through body 3

        Fixture fixture3 = body3.createFixture(fixtureDef);
        fixture3.setUserData("Score");


        shape.dispose();
    }

    public void drawPipes(SpriteBatch batch) {
        batch.draw(pipe1, pipe1.getX() - pipe1.getWidth() / 2f,
            pipe1.getY() - pipe1.getHeight() / 2f );

        batch.draw(pipe2, pipe2.getX() - pipe2.getWidth() / 2,
            pipe2.getY() - pipe2.getHeight() / 2);

    }

    public void updatePipes() {
        pipe1.setPosition(body1.getPosition().x * GameInfo.PPM,
               body1.getPosition().y * GameInfo.PPM);

        pipe2.setPosition(body2.getPosition().x * GameInfo.PPM,
            body2.getPosition().y * GameInfo.PPM);
    }

    public void movePipes() {
        body1.setLinearVelocity(-1, 0); // moves to left
        body2.setLinearVelocity(-1,0);
        body3.setLinearVelocity(-1,0);

        //As pipe1 and pipe2 are in same x position, they differ in y position.
        // if pipes go out of screen it must be stopped for performance
        if(pipe1.getX() + (GameInfo.WIDTH / 2f) + 60 < mainCamera.position.x) {
           // System.out.println("Out of screen");
            // System.out.println("x position" +pipe1.getX());
            body1.setActive(false);
            body2.setActive(false);
            body3.setActive(false);
        }
    }

    public void stopPipes() { // used in collision control
        body1.setLinearVelocity(0, 0); // moves to left
        body2.setLinearVelocity(0,0);
        body3.setLinearVelocity(0,0);

    }

    public void setMainCamera(OrthographicCamera mainCamera) {
        this.mainCamera = mainCamera;
    }

    float getRandomY() {
        float max = GameInfo.HEIGHT / 2f + 150f; // 800 / 2 = 400 + 150 = 550
        float min = GameInfo.HEIGHT / 2f - 150f; //  800 / 2 = 400 - 150 = 250

        //this line returns random number between 250 and 550.
        return random.nextFloat() * (max - min) + min;

    }

    public void disposeAll() {
        pipe1.getTexture().dispose();
        pipe2.getTexture().dispose();
    }




}
