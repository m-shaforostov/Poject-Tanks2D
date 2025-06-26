package com.example.projecttanks;

import javafx.scene.media.AudioClip;
import java.io.File;

/**
 * Helper class that plays sounds in the game.
 */
public class SoundPlayer {
    private final String bulletBounce = "BulletBounce.wav";
    private final String emptyBarrel = "EmptyBarrel.wav";
    private final String fireBullet = "FireBullet.wav";
    private final String mineExplosion = "MineExplosion.wav";
    private final String tankExplosion = "TankExplosion.wav";
    private final String tankLand = "TankLand.wav";

    private final AudioClip bulletBounceSound;
    private final AudioClip emptyBarrelSound;
    private final AudioClip fireBulletSound;
    private final AudioClip mineExplosionSound;
    private final AudioClip tankExplosionSound;
    private final AudioClip tankLandSound;

    SoundPlayer() {
        bulletBounceSound = initMedia(bulletBounce);
        emptyBarrelSound = initMedia(emptyBarrel);
        fireBulletSound = initMedia(fireBullet);
        mineExplosionSound = initMedia(mineExplosion);
        tankExplosionSound = initMedia(tankExplosion);
        tankLandSound = initMedia(tankLand);
    }

    private AudioClip initMedia(String filename) {
        return new AudioClip(new File(filename).toURI().toString());
    }

    /** Plays the sound of bullet bouncing from a wall */
    public void playBulletBounce() { bulletBounceSound.play(); }
    /** Plays the sound that indicates that the barrel is empty */
    public void playEmptyBarrel() { emptyBarrelSound.play(); }
    /** Plays the sound of shooting */
    public void playFireBullet() { fireBulletSound.play(); }
    /** Plays the sound of explosion of the tank */
    public void playTankExplosion() { tankExplosionSound.play(); }
    /** Plays the sound of appearance of the tank on the battlefield */
    public void playTankLand() { tankLandSound.play(); }
    public void playMineExplosion() { mineExplosionSound.play(); }
}
