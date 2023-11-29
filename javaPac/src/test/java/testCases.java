import org.junit.Assert;
import org.junit.Test;

public class testCases {

    @org.junit.Test
    public void gameStartWithFalse(){
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        Assert.assertFalse(gamePlay.initGame());
    }

    @org.junit.Test
    public void playerMove(){
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        gamePlay.initGame();
        gamePlay.game.inGame = true;
        int pacx = gamePlay.game.pacman_x;
        gamePlay.game.req_dx = 1;
        gamePlay.game.req_dy = 0;
        gamePlay.movePacman();
        Assert.assertTrue(gamePlay.game.pacman_x != pacx);
    }

    @org.junit.Test
    public void playerOnGhost(){
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        gamePlay.initGame();
        gamePlay.game.inGame = true;
        gamePlay.game.pacman_x = gamePlay.game.ghost_x[0];
        gamePlay.game.pacman_y = gamePlay.game.ghost_y[0];
        gamePlay.moveGhosts();
        Assert.assertTrue(gamePlay.game.dying);
    }

    @org.junit.Test
    public void playerOnBlock(){
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        gamePlay.initGame();
        gamePlay.game.screenData[0] = 2;
        gamePlay.game.inGame = true;
        gamePlay.movePacman();
        Assert.assertTrue(gamePlay.game.dying);
    }

    @org.junit.Test
    public void playerOnEat(){
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        gamePlay.initGame();
        gamePlay.game.screenData[0] = 16;
        gamePlay.game.inGame = true;
        gamePlay.movePacman();
        Assert.assertTrue(gamePlay.game.score > 0);
    }

    @org.junit.Test
    public void playerOnEgg() {
        GamePlay gamePlay = new GamePlay(new Game());
        gamePlay.initVariables();
        gamePlay.initGame();
        gamePlay.game.egpos = 0;
        gamePlay.game.screenData[0] = 4;
        gamePlay.game.inGame = true;
        gamePlay.movePacman();
        Assert.assertEquals(50, gamePlay.game.score);
    }
}
