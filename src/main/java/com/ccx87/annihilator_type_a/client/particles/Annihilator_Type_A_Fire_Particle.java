package com.ccx87.annihilator_type_a.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class Annihilator_Type_A_Fire_Particle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private float angularVelocity;
    private final float angularAcceleration;

    protected Annihilator_Type_A_Fire_Particle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2F, 0.2F);
        this.quadSize *= 1.5F;
        this.lifetime = Math.max(1, 20 + (this.random.nextInt(80) - 40));
        this.gravity = -0.01F;
        this.hasPhysics = true;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.angularVelocity = 0.001F;
        this.angularAcceleration = 0.001F;
        this.setSpriteFromAge(spriteSet);
    }


    public int getLightColor(float partialTick) {
        return 15728880;
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += this.angularVelocity;
        this.angularVelocity += this.angularAcceleration;
        if (!this.removed) {
            this.setSprite(this.spriteSet.get(this.age / 2 % 12 + 1, 20));
        }

    }

    public static record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public @NotNull Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new Annihilator_Type_A_Fire_Particle(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
