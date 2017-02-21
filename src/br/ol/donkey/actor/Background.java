package br.ol.donkey.actor;

import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import br.ol.donkey.infra.Actor;
import br.ol.g2d.Sprite;
import java.awt.Graphics2D;

/**
 * Background actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Background extends Actor<DonkeyGame> {
    
    private Sprite background;
    
    public Background(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        background = game.getG2D().getSpriteSheet().getSprite("background");
    }

    @Override
    protected void draw(Graphics2D g) {
        background.draw(g);
    }
    
    public void stateChanged() {
        setVisible(game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.LEVEL_CLEARED
                || game.getState() == State.GAME_OVER);
    }
    
}
