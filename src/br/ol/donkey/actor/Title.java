package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import java.awt.event.KeyEvent;

/**
 * Title actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Title extends DonkeyActor {

    public Title(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
        setAnimation("title");
    }

    @Override
    protected void updateTitle() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    game.getTextBitmapScreen().clearScreen();
                    game.getTextBitmapScreen().offsetY = 4;
                    getAnimation().play();
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().currentFrameIndex < 65) {
                        break yield;
                    }
                    game.getTextBitmapScreen().print(2, 30, "PROGRAMMED BY O.L.  2017");
                    game.setTextBitmapScreenVisible(true);
                    instructionPointer = 2;
                case 2:
                    if (((long) (System.nanoTime() * 0.000000005)) % 2 == 0) {
                        game.getTextBitmapScreen().print(4, 26, "PRESS SPACE TO START");
                    }
                    else {
                        game.getTextBitmapScreen().print(4, 26, "                    ");
                    }
                    if (isKeyPressed(KeyEvent.VK_SPACE)) {
                        game.startNewGame();
                    }
                    
                    if (!getAnimation().playing) {
                        game.setState(State.OL_PRESENTS);
                    }
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.TITLE);
        setVisible(game.getState() == State.TITLE);
        game.setTextBitmapScreenVisible(game.getState() == State.TITLE);
        if (game.getState() == State.TITLE) {
            getAnimation().currentFrameIndex = 0;
            instructionPointer = 0;
        }
    }
    
}
