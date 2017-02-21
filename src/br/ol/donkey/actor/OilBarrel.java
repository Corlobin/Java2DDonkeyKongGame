package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;

/**
 * OilBarrel actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class OilBarrel extends DonkeyActor {

    public OilBarrel(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setKinematic(true);
        setAnimation("oil_barrel_fire_extinguished");
    }

    @Override
    public void updatePlaying() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    break yield;
                // fire strong
                case 1:
                    playAnimation("oil_barrel_fire_strong");
                    instructionPointer = 2;
                case 2:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    game.spawnFireEnemy();
                    instructionPointer = 3;
                case 3:
                    playAnimation("oil_barrel_fire_weak");
                    instructionPointer = 0;
                    break yield;
            }
        }
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.GAME_OVER);
        setVisible(game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.GAME_OVER);
    }
    
    public void spawnFireEnemy() {
        instructionPointer = 1;
    }
    
}
