package br.ol.donkey;

import br.ol.donkey.actor.Background;
import br.ol.donkey.actor.Barrel;
import br.ol.donkey.actor.BarrelJumpPointCollider;
import br.ol.donkey.actor.Barrels;
import br.ol.donkey.actor.Donkey;
import br.ol.donkey.actor.FireEnemy;
import br.ol.donkey.actor.GameOver;
import br.ol.donkey.actor.HUD;
import br.ol.donkey.actor.Hammer;
import br.ol.donkey.actor.Initialization;
import br.ol.donkey.actor.Intro;
import br.ol.donkey.actor.IntroHowHigh;
import br.ol.donkey.actor.LevelCleared;
import br.ol.donkey.actor.Mario;
import br.ol.donkey.actor.OLPresents;
import br.ol.donkey.actor.OilBarrel;
import br.ol.donkey.actor.Princess;
import br.ol.donkey.actor.ScorePoint;
import br.ol.donkey.actor.Spark;
import br.ol.donkey.actor.Title;
import br.ol.g2d.G2DContext;
import br.ol.donkey.infra.Actor;
import br.ol.donkey.infra.Game;
import br.ol.donkey.infra.SpritePixelCollider;
import br.ol.g2d.TextBitmapScreen;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DonkeyGame class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class DonkeyGame extends Game {
    
    protected final G2DContext g2d = new G2DContext();
    
    // physics
    protected final double gravity = 0.085;
    
    // collider
    public static final boolean DEBUG_DRAW_COLLIDERS = false;
    
    public static final int COLLIDING_WITH_LEVEL_CLEARED = 0xFF000000;
    public static final int COLLIDING_WITH_FLOOR = 0xFFFF0000;
    public static final int COLLIDING_WITH_STAIR_LEFT = 0xFF00FF00;
    public static final int COLLIDING_WITH_STAIR_RIGHT = 0xFF0000FF;
    
    protected SpritePixelCollider terrainCollider;
    protected Map<Class<? extends Actor>, Map<Class<? extends Actor>, String>>  collisionResponses 
            = new HashMap<Class<? extends Actor>, Map<Class<? extends Actor>, String>>();

    // game state
    public static enum State { 
        INITIALIZING, 
        POWERED_BY_G2D, 
        OL_PRESENTS, 
        TITLE, 
        INTRO, 
        INTRO_HOW_HIGH, 
        PLAYING, 
        PLAYING_DESTROYING_ENEMY, 
        PLAYING_MARIO_START_DYING, 
        PLAYING_MARIO_DIED, 
        LEVEL_CLEARED, 
        GAME_OVER }
    
    protected State state = State.INITIALIZING;

    // actors
    protected Spark spark;
    protected List<DonkeyActor> cachedActors = new ArrayList<DonkeyActor>();
    protected int fireEnemiesCount;
    
    // bitmap text screen
    protected boolean textBitmapScreenVisible = false;
    protected TextBitmapScreen textBitmapScreen;

    // hud
    protected long startGameTime;
    protected int score = 0;
    protected int hiScore = 0;
    protected int lives = 3;
    protected int currentLevel = 1;
    protected double bonus = 50;
    
    public DonkeyGame() {
        screenSize = new Dimension(224, 256);
        screenScale = new Point2D.Double(2, 2);
        terrainCollider = new SpritePixelCollider(null, null);
    }

    public G2DContext getG2D() {
        return g2d;
    }

    public double getGravity() {
        return gravity;
    }

    public SpritePixelCollider getTerrainCollider() {
        return terrainCollider;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            broadcastMessage("stateChanged");
        }
    }

    public boolean isTextBitmapScreenVisible() {
        return textBitmapScreenVisible;
    }

    public void setTextBitmapScreenVisible(boolean textBitmapScreenVisible) {
        this.textBitmapScreenVisible = textBitmapScreenVisible;
    }

    public TextBitmapScreen getTextBitmapScreen() {
        return textBitmapScreen;
    }

    public int getFireEnemiesCount() {
        return fireEnemiesCount;
    }
    
    public void incFireEnemiesCount() {
        fireEnemiesCount++;
    }

    public void decFireEnemiesCount() {
        fireEnemiesCount--;
    }
    
    public void resetFireEnemiesCount() {
        fireEnemiesCount = 0;
    }
    
    @Override
    public void init() {
        loadG2DContext();
        setTextBitmapScreen();
        setTerrainCollider();
        registerAllCollisionResponses();
        createAllActors();
        initAllActors();
        
       // setState(State.PLAYING);
    }
    
    private void loadG2DContext() {
        try {
            g2d.loadFromResource("/res/donkey.g2d");
        } catch (Exception ex) {
            Logger.getLogger(DonkeyActor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    private void setTextBitmapScreen() {
        textBitmapScreen = g2d.getTextScreens().get("screen");
    }
    
    private void setTerrainCollider() {
        terrainCollider.setSprite(g2d.getSpriteSheet().getSprite("terrain_collision_map"));
    }
    
    private void registerAllCollisionResponses() {
        registerCollisionResponse(Mario.class, FireEnemy.class, "");
        registerCollisionResponse(Mario.class, Barrel.class, "");
        registerCollisionResponse(Mario.class, BarrelJumpPointCollider.class, "");
        registerCollisionResponse(Mario.class, Hammer.class, "marioStartPowerUp");
    }
    
    private void createAllActors() {
        actors.add(new Initialization(this));
        actors.add(new Background(this));
        actors.add(new Mario(this));
        actors.add(new Princess(this));
        actors.add(new Hammer(this, 17, 96));
        actors.add(new Hammer(this, 168, 190));
        actors.add(new Donkey(this));
        actors.add(spark = new Spark(this));
        initializeCachedActor(Barrel.class, 20);
        for (Actor barrel : cachedActors) {
            if (barrel instanceof Barrel) {
                actors.add(((Barrel) barrel).getBarrelJumpPointCollider());
            }
        }
        initializeCachedActor(FireEnemy.class, 3);
        initializeCachedActor(ScorePoint.class, 20);
        actors.add(new Barrels(this));
        actors.add(new OilBarrel(this));
        
        actors.add(new OLPresents(this));
        actors.add(new Title(this));
        actors.add(new Intro(this));
        actors.add(new IntroHowHigh(this));
        actors.add(new LevelCleared(this));
        actors.add(new GameOver(this));
        actors.add(new HUD(this));
    }
    
    private void initAllActors() {
        for (Actor actor : actors) {
            actor.init();
        }
    }

    public void registerCollisionResponse(Class<? extends Actor> actorType1, Class<? extends Actor> actorType2, String message) {
        Map<Class<? extends Actor>, String> collisionResponse = collisionResponses.get(actorType1);
        if (collisionResponse == null) {
            collisionResponse = collisionResponses.get(actorType2);
            if (collisionResponse == null) {
                collisionResponse = new HashMap<Class<? extends Actor>, String>();
                collisionResponses.put(actorType1, collisionResponse);
            }
            else {
                Class<? extends Actor> actorTypeTmp = actorType1;
                actorType1 = actorType2;
                actorType2 = actorTypeTmp;
            }
        }
        String registeredMessage = collisionResponse.get(actorType2);
        if (registeredMessage != null) {
            throw new RuntimeException("Collision response already registered !");
        }
        collisionResponse.put(actorType2, message);
    }
    
    public void checkCollisions() {
        for (Actor actor1 : actors) {
            if (!actor1.isVisible() || !(actor1 instanceof DonkeyActor)) {
                continue;
            }
            DonkeyActor donkeyActor1 = (DonkeyActor) actor1;
            if (!donkeyActor1.isCollisionCheckEnabled()) {
                continue;
            }
            if (collisionResponses.containsKey(actor1.getClass())) {
                Map<Class<? extends Actor>, String> collisionResponse = collisionResponses.get(actor1.getClass());
                for (Actor actor2 : actors) {
                    if (!actor2.isVisible() || actor1 == actor2 || !(actor2 instanceof DonkeyActor)) {
                        continue;
                    }
                    String registeredMessage = collisionResponse.get(actor2.getClass());
                    DonkeyActor donkeyActor2 = (DonkeyActor) actor2;
                    if (!donkeyActor2.isCollisionCheckEnabled()) {
                        continue;
                    }
                    SpritePixelCollider colliderActor1 = donkeyActor1.getActorCollider();
                    SpritePixelCollider colliderActor2 = donkeyActor2.getActorCollider();
                    if (registeredMessage != null && colliderActor1.collides(colliderActor2)) {
                        broadcastMessage(registeredMessage);
                        donkeyActor1.onCollision(donkeyActor2, colliderActor1.getLastCollisionScreenPoint());
                        donkeyActor2.onCollision(donkeyActor1, colliderActor1.getLastCollisionScreenPoint());
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        super.update(); 
        checkCollisions();
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (DEBUG_DRAW_COLLIDERS) {
            terrainCollider.draw(g);
        }
        super.draw(g);
        if (textBitmapScreenVisible) {
            textBitmapScreen.draw(g);
        }
    }

    // --- hud ---
    
    public String getScore() {
        String scoreStr = "000000" + score;
        scoreStr = scoreStr.substring(scoreStr.length() - 6, scoreStr.length());
        return scoreStr;
    }

    public String getHiScore() {
        String hiScoreStr = "000000" + hiScore;
        hiScoreStr = hiScoreStr.substring(hiScoreStr.length() - 6, hiScoreStr.length());
        return hiScoreStr;
    }

    public void clearScore() {
        score = 0;
    }
    
    public void addScorePoint(int scorePoint) {
        score += scorePoint;
    }
    
    public void updateHiScore() {
        if (score > hiScore) {
            hiScore = score;
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getCurrentLevelStr() {
        String currentLevelStr = "00" + currentLevel;
        currentLevelStr = currentLevelStr.substring(currentLevelStr.length() - 2, currentLevelStr.length());
        return currentLevelStr;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getBonusStr() {
        String bonusStr = "0000" + ((int) bonus) * 100;
        bonusStr = bonusStr.substring(bonusStr.length() - 4, bonusStr.length());
        return bonusStr;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        bonus = bonus > 50 ? 50 : bonus;
        bonus = bonus < 0 ? 0 : bonus;
        this.bonus = bonus;
    }

    // --- cached actors
    
    private void initializeCachedActor(Class<? extends DonkeyActor> actorType, int size) {
        for (int c = 0; c < size; c++) {
            try {
                Constructor<? extends DonkeyActor> actorConstructor = actorType.getConstructor(DonkeyGame.class);
                DonkeyActor newCachedActor = actorConstructor.newInstance(this);
                newCachedActor.setDestroyed(true);
                newCachedActor.setVisible(false);
                cachedActors.add(newCachedActor);
                actors.add(newCachedActor);
            } catch (Exception ex) {
                Logger.getLogger(DonkeyGame.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
        }
    }

    private <T extends DonkeyActor> T getActorFromCache(Class<T> actorType) {
        for (DonkeyActor cachedActor : cachedActors) {
            if (cachedActor.isDestroyed() && !cachedActor.isVisible() && actorType.isInstance(cachedActor)) {
                return (T) cachedActor;
            }
        }
        throw new RuntimeException("Insufficient cache size for " + actorType.getName() + " type !");
    }
        
    // --- spawn ---
        
    public void showSpark(int x, int y, int points) {
        spark.show(x, y, points);
    }
    
    public void showScorePoint(int x, int y, int points) {
        ScorePoint scorePoint = getActorFromCache(ScorePoint.class);
        scorePoint.show(x, y, points);
        addScorePoint(points);
    }

    public void spawnFirstBlueBarrel() {
        Barrel barrelBlue = getActorFromCache(Barrel.class);
        barrelBlue.spawnFirstBlue();
    }

    public void spawnBarrel(boolean isBlue) {
        Barrel barrelBlue = getActorFromCache(Barrel.class);
        barrelBlue.spawn(isBlue);
    }

    public void spawnFireEnemy() {
        FireEnemy fireEnemy = getActorFromCache(FireEnemy.class);
        fireEnemy.spawn();
    }
    
    // --- game flow ---
    
    public void startNewGame() {
        startGameTime = System.currentTimeMillis();
        setCurrentLevel(1);
        setLives(3);
        setBonus(55);
        setState(State.INTRO);
    }
    
    public void marioDied() {
        lives--;
        if (lives == 0) {
            setState(State.GAME_OVER);
        }
        else {
            setState(State.INTRO_HOW_HIGH);
        }
    }
    
    public void levelCleared() {
        setState(State.LEVEL_CLEARED);
    }

    public void goToNextLevel() {
        currentLevel++;
        addScorePoint(((int) bonus) * 100);
        setBonus(55);
        setState(State.INTRO_HOW_HIGH);
    }
    
    public void returnToTitle() {
        setCurrentLevel(1);
        setLives(3);
        setBonus(55);
        setState(State.TITLE);
    }
    
}
