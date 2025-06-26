package com.example.projecttanks;

import javafx.scene.media.AudioClip;
import java.io.File;

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


    public void playBulletBounce() { bulletBounceSound.play(); }
    public void playEmptyBarrel() { emptyBarrelSound.play(); }
    public void playFireBullet() { fireBulletSound.play(); }
    public void playTankExplosion() { tankExplosionSound.play(); }
    public void playTankLand() { tankLandSound.play(); }
    public void playMineExplosion() { mineExplosionSound.play(); }
}
