package br.ol.donkey;


import br.ol.donkey.infra.Actor;
import br.ol.donkey.infra.Keyboard;
import br.ol.donkey.infra.SpritePixelCollider;
import br.ol.donkey.infra.Time;
import br.ol.g2d.Animation;
import br.ol.g2d.AnimationObjectFrame;
import br.ol.g2d.AnimationSpriteObject;
import br.ol.g2d.Sprite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * DonkeyActor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class DonkeyActor extends Actor<DonkeyGame> {
    
    private boolean paused; // true = freezes animation & physics
    private boolean destroyed = true; // for caching purposes
    
    // physics
    private double vx, vy, restitution;
    private final AffineTransform transform = new AffineTransform();
    private boolean affectedByGravity = true;
    private boolean affectedByTerrain = true;
    private boolean kinematic = false; // true = not affected by "physics engine"
    
    // animation
    private Animation animation;
    private AnimationObjectFrame currentAnimationFrame;
    private final Map<String, Animation> animations = new HashMap<String, Animation>();
    
    // colliders
    private boolean collisionCheckEnabled = true;
    private SpritePixelCollider actorCollider;
    private SpritePixelCollider terrainCollider;
    
    // direction
    public static enum Direction { LEFT, RIGHT, NONE };
    private Direction lastDirection = Direction.NONE;
    
    public DonkeyActor(DonkeyGame game) {
        super(game);
        createColliders();
    }

    private void createColliders() {
        actorCollider = new SpritePixelCollider(this, null);
        terrainCollider = new SpritePixelCollider(this, null);
        int nonCollidingColor = game.getG2D().getSpriteSheet().getBackgroundColor().getRGB();
        actorCollider.setNonCollidingColor(nonCollidingColor);
        terrainCollider.setNonCollidingColor(nonCollidingColor);
    }
    
    @Override
    public void init() {
        set(0, 0);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public double getX() {
        if (kinematic && animation != null && currentAnimationFrame != null) {
            return currentAnimationFrame.x;
        }
        return super.getX();
    }

    @Override
    public double getY() {
        if (kinematic && animation != null && currentAnimationFrame != null) {
            return currentAnimationFrame.y;
        }
        return super.getY();
    }
    
    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
        lastDirection = vx < 0 ? Direction.LEFT : vx > 0 ? Direction.RIGHT : lastDirection;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    public boolean isAffectedByTerrain() {
        return affectedByTerrain;
    }

    public void setAffectedByTerrain(boolean affectedByTerrain) {
        this.affectedByTerrain = affectedByTerrain;
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public void setKinematic(boolean kinematic) {
        this.kinematic = kinematic;
    }

    public Animation getAnimation() {
        return animation;
    }

    public AnimationObjectFrame getCurrentAnimationFrame() {
        return currentAnimationFrame;
    }

    public Map<String, Animation> getAnimations() {
        return animations;
    }
    
    protected void setAnimation(String name) {
        if (animation != null && animation.getName().equals(name)) {
            return;
        }
        animation = animations.get(name);
        currentAnimationFrame = null;
        if (animation == null) {
            animation = game.getG2D().getAnimations().getCopy(name);
        }
        if (animation != null) {
            animations.put(name, animation);
            updateAnimationFrame();
            updateCollider();
        }
    }

    protected void setEmptyAnimation() {
        animation = null;
    }

    protected void playAnimation(String name) {
        setAnimation(name);
        if (animation != null && !animation.playing) {
            animation.play();
        }
    }
        
    protected boolean isAnimationPlaying() {
        return animation != null && animation.playing;
    }

    public boolean isCollisionCheckEnabled() {
        return collisionCheckEnabled;
    }

    public void setCollisionCheckEnabled(boolean collisionCheckEnabled) {
        this.collisionCheckEnabled = collisionCheckEnabled;
    }

    public SpritePixelCollider getActorCollider() {
        return actorCollider;
    }

    public SpritePixelCollider getTerrainCollider() {
        return terrainCollider;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }
    
    @Override
    public void update() {
        switch (game.getState()) {
            case INITIALIZING: updateInitializing(); break;
            case POWERED_BY_G2D: updatePoweredByG2D(); break;
            case OL_PRESENTS: updateOLPresents(); break;
            case TITLE: updateTitle(); break;
            case INTRO: updateIntro(); break;
            case INTRO_HOW_HIGH: updateIntroHowHigh(); break;
            case PLAYING: updatePlaying(); break;
            case PLAYING_DESTROYING_ENEMY: updatePlayingDestroyingEnemy(); break;
            case PLAYING_MARIO_START_DYING: updatePlayingMarioStartDying(); break;
            case PLAYING_MARIO_DIED: updatePlayingMarioDied(); break;
            case LEVEL_CLEARED: updateLevelCleared(); break;
            case GAME_OVER: updateGameOver(); break;
        }
    }
    
    @Override
    protected void internalUpdate() {
        if (paused) {
            update();
        }
        else if (!isUpdatebleJustWhenVisible() || (isUpdatebleJustWhenVisible() && isVisible())) {
            updateAnimation();
            updateCollider();
            update();
            if (!kinematic) {
                updatePhysicsGravity();
            }
            if (affectedByTerrain) {
                updatePhysicsCollisionWithTerrain();
            }
            else {
                translate(vx, vy);
            }
        }
    }
    
    protected void updatePhysicsGravity() {
        if (affectedByGravity) {
            vy += game.getGravity();
        }
    }
    
    protected void updateAnimationFrame() {
        if (animation != null) {
            AnimationSpriteObject animationSpriteObject = (AnimationSpriteObject) animation.objects.get(0);
            currentAnimationFrame = animationSpriteObject.getFrame(animation.currentFrameIndex);
        }
    }
    
    protected void updateAnimation() {
        if (animation != null) {
            animation.update(Time.delta);
            updateAnimationFrame();
        }
    }
    
    protected void updateCollider() {
        if (animation != null) {
            int currentFrameIndex = animation.currentFrameIndex;
            AnimationSpriteObject animationSpriteObject = (AnimationSpriteObject) animation.objects.get(0);
            Sprite currentSprite = animationSpriteObject.getSprite(currentFrameIndex);
            if (actorCollider.getSprite() != currentSprite) {
                actorCollider.setSprite(currentSprite);
            }
            if (terrainCollider.getSprite() != currentSprite) {
                terrainCollider.setSprite(currentSprite);
                terrainCollider.setBottomCheckArea(5);
            }
        }        
    }
    
    public void updatePhysicsCollisionWithTerrain() {
        double ox = super.getX();
        double oy = super.getY();
        double step = 1 / Math.sqrt(vx * vx + vy * vy);
        step = step <= 0 || step > 1 ? 1 : step;
        for (double p = step; p <= 1; p += step) {
            setY(oy + vy * p);
            if (isCollidingWithFloor()) {
                setY(oy + vy * (p - step));
                if (vy > 0) {
                    vy = -vy * restitution;
                }
            }
            setX(ox + vx * p);
            if (isCollidingWithFloor()) {
                translate(0, -1);
            }
        }        
    }

    @Override
    public void draw(Graphics2D g) {
        if (animation == null) {
            return;
        }
        if (kinematic) {
            animation.draw(g);
        }
        else {
            AffineTransform at = g.getTransform();
            transform.setToIdentity();
            transform.translate((int) super.getX(), (int) super.getY());
            if (currentAnimationFrame != null) {
                transform.translate(-currentAnimationFrame.x, -currentAnimationFrame.y);
            }
            g.transform(transform);
            animation.draw(g);
            g.setTransform(at);
            
            if (DonkeyGame.DEBUG_DRAW_COLLIDERS) {
                actorCollider.draw(g);
                terrainCollider.draw(g);
            }
        }
    }

    // --- collision detection ---

    public void onCollision(DonkeyActor collidedActor, Point collisionPoint) {
    }
    
    public boolean isCollidingWithLevelCleared() {
        return game.getTerrainCollider().collides(terrainCollider, DonkeyGame.COLLIDING_WITH_LEVEL_CLEARED);
    }

    public boolean isCollidingWithFloor() {
        return game.getTerrainCollider().collides(terrainCollider, DonkeyGame.COLLIDING_WITH_FLOOR);
    }
    
    public boolean isCollidingWithFloorUp() {
        return game.getTerrainCollider().collides(0, -1, terrainCollider, DonkeyGame.COLLIDING_WITH_FLOOR);
    }

    public boolean isCollidingWithFloorDown() {
        return game.getTerrainCollider().collides(0, 1, terrainCollider, DonkeyGame.COLLIDING_WITH_FLOOR);
    }

    public boolean isCollidingWithStair() {
        return game.getTerrainCollider().collides(terrainCollider
                , DonkeyGame.COLLIDING_WITH_STAIR_LEFT, DonkeyGame.COLLIDING_WITH_STAIR_RIGHT);
    }

    public boolean canKeepClimbingStair() {
        return game.getTerrainCollider().collides(0, -1, terrainCollider
                , DonkeyGame.COLLIDING_WITH_STAIR_LEFT, DonkeyGame.COLLIDING_WITH_STAIR_RIGHT);
    }

    public boolean canDownStair() {
        return game.getTerrainCollider().collides(0, 6, terrainCollider
                , DonkeyGame.COLLIDING_WITH_STAIR_LEFT, DonkeyGame.COLLIDING_WITH_STAIR_RIGHT);
    }
    
    public boolean canActorDownStair() {
        return game.getTerrainCollider().collides(terrainCollider, 0xFFFF00FF);
    }
    
    // --- keyboard input ---
    
    protected boolean isKeyDown(int keyCode) {
        return Keyboard.keyPressed[keyCode];
    }

    protected boolean isKeyPressed(int keyCode) {
        if (Keyboard.keyPressed[keyCode] && !Keyboard.keyPressedConsumed[keyCode]) {
            Keyboard.keyPressedConsumed[keyCode] = true;
            return true;
        }
        else {
            return false;
        }
    }
    
    // --- updates ---
    
    protected void updateInitializing() {
    }

    protected void updatePoweredByG2D() {
    }

    protected void updateOLPresents() {
    }

    protected void updateTitle() {
    }

    protected void updateIntro() {
    }

    protected void updateIntroHowHigh() {
    }

    protected void updatePlaying() {
    }

    protected void updatePlayingDestroyingEnemy() {
    }

    protected void updateLevelCleared() {
    }

    protected void updatePlayingMarioStartDying() {
    }

    protected void updatePlayingMarioDied() {
    }

    protected void updateGameOver() {
    }
    
    // --- broadcast messages ---
    
    public void stateChanged() {
    }
    
    public void hideAll() {
        setVisible(false);
    }
    
    public void showAll() {
        setVisible(true);
    }
    
}
