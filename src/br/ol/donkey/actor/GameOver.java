package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;

/**
 * GameOver actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class GameOver extends DonkeyActor {

    public GameOver(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
    }

    @Override
    protected void updateGameOver() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    setAnimation("game_over");
                    getAnimation().stop();
                    getAnimation().play();
                    game.getTextBitmapScreen().clearScreen();
                    game.getTextBitmapScreen().offsetY = 4;
                    game.getTextBitmapScreen().print(9, 15, "GAME  OVER");
                    game.setTextBitmapScreenVisible(true);
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 2;
                case 2:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    game.setTextBitmapScreenVisible(false);
                    setUpdatebleJustWhenVisible(false);
                    game.updateHiScore();
                    game.clearScore();
                    game.broadcastMessage("hideAll");
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 3;
                case 3:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    game.returnToTitle();
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.GAME_OVER);
        setVisible(game.getState() == State.GAME_OVER);
        if (game.getState() == State.GAME_OVER) {
            instructionPointer = 0;
        }
    }
    
}
