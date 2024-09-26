package hud;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.krishna.birdtwo.GameMain;

import helpers.GameInfo;

public class UIHud {

    //variables
    private GameMain game;
    private Stage stage;
    private Viewport gameViewport;


    //constructor
    public UIHud(GameMain game) {
        this.game = game;
        gameViewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT,
            new OrthographicCamera());

        stage = new Stage(gameViewport, game.getBatch());

    }

    //getter
    public Stage getStage() {
        return this.stage;
    }










} //UIHud
