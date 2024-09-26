package bird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Bird extends Sprite {

    //variable declration
    private World world;
    private Body body;

    //collision
    private boolean isAlive; // default value is false for boolean.

    //constructor
    public Bird(World world, float x, float y){
        super(new Texture("Birds/Blue/Idle.png"));
        this.world = world;
        setPosition(x,y);
        createBody();
        isAlive = true;
    }

    //custom method
    void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; //dynamic body by default affected by gravity.
        bodyDef.position.set(getX() /  GameInfo.PPM, getY() / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(false);

        CircleShape shape = new CircleShape();
        shape.setRadius((getHeight() / 2f) / GameInfo.PPM );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        //collide
        fixtureDef.filter.categoryBits = GameInfo.BIRD;
        //bird will collide with ground, pipe and score.
        fixtureDef.filter.maskBits = GameInfo.GROUND | GameInfo.PIPE | GameInfo.SCORE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Bird");

        shape.dispose();
       // body.setActive(false); // used for touch the screen then start the game.
    }

//    public void activateBird() { // used for first touch and start the game
//        isAlive = true;
//        body.setActive(true);
//    }

    public void birdFlapJump() {
        body.setLinearVelocity(0, 3); // moving bird by tapping y - up, x - moving left or right
    }

    public void drawIdle(SpriteBatch batch) {
        //batch.draw(this, getX(), getY()); //draws circle shape below bird

        //to position exactly circle shape to bird use this code
        batch.draw(this, getX() - getWidth() / 2f, getY() - getHeight() / 2f );
    }

    public void updateBird() {
        setPosition(body.getPosition().x * GameInfo.PPM, body.getPosition().y * GameInfo.PPM);
    }

    //creating getter and setter for collision

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getAlive() {
        return isAlive;
    }



}// bird
