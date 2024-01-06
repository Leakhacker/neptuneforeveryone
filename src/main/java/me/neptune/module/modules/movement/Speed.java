package me.neptune.module.modules.movement;

import me.neptune.cmd.CommandManager;
import me.neptune.events.eventbus.EventHandler;
import me.neptune.events.eventbus.EventPriority;
import me.neptune.events.impl.MoveEvent;
import me.neptune.events.impl.PacketEvent;
import me.neptune.events.impl.UpdateWalkingEvent;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.MovementUtil;
import me.neptune.utils.Timer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.lang.reflect.Field;

public class Speed extends Module {
    private final BooleanSetting jump =
            new BooleanSetting("Jump", "speed_jump", true);
    private final BooleanSetting inWater =
            new BooleanSetting("InWater", "speed_inwater",false);
    private final SliderSetting strafeSpeed =
            new SliderSetting("StrafeSpeed", "speed_speed", 287.3, 100.0, 1000.0, 0.1);
    private final SliderSetting strafeY =
            new SliderSetting("StrafeY", "speed_strafey", 0.99f, 0.1f, 1.2f, 0.01);
    private final BooleanSetting explosions =
            new BooleanSetting("Explosions", "speed_explosions",false);
    private final BooleanSetting velocity =
            new BooleanSetting("Velocity", "speed_velocity",false);
    private final SliderSetting multiplier =
            new SliderSetting("H-Factor", "speed_h",1.0f, 0.0f, 5.0f, 0.1);
    private final SliderSetting vertical =
            new SliderSetting("V-Factor", "speed_v",1.0f, 0.0f, 5.0f, 0.1);
    private final SliderSetting coolDown =
            new SliderSetting("CoolDown", "speed_cd",1000, 0, 5000, 1);
    private final SliderSetting lagTime =
            new SliderSetting("LagTime", "speed_lagtime", 500, 0, 1000, 1);
    private final SliderSetting cap =
            new SliderSetting("Cap", "speed_cap",10.0, 0.0, 10.0, 0.1);
    private final BooleanSetting scaleCap =
            new BooleanSetting("ScaleCap", "speed_scalecap",false);
    private final BooleanSetting slow =
            new BooleanSetting("Slowness", "speed_slow",false);
    private final BooleanSetting debug =
            new BooleanSetting("Debug", "speed_debug",false);
    private final Timer expTimer = new Timer();
    private final Timer lagTimer = new Timer();
    private boolean stop;
    private double speed;
    private double distance;

    private int stage;
    private double lastExp;
    private boolean boost;

    public Speed() {
        super("Speed", Category.Movement);
        try {
            for (Field field : Speed.class.getDeclaredFields()) {
                if (!Setting.class.isAssignableFrom(field.getType()))
                    continue;
                Setting setting = (Setting) field.get(this);
                addSetting(setting);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            speed = MovementUtil.getSpeed(false);
            distance = MovementUtil.getDistance2D();
        }

        stage = 4;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invoke(PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (mc.player != null
                    && packet.getId() == mc.player.getId()
                    && this.velocity.getValue()) {
                double speed = Math.sqrt(
                        packet.getVelocityX() * packet.getVelocityX()
                                + packet.getVelocityZ() * packet.getVelocityZ())
                        / 8000.0;

                this.lastExp = this.expTimer
                        .passedMs(this.coolDown.getValueInt())
                        ? speed
                        : (speed - this.lastExp);

                if (this.lastExp > 0) {
                    this.expTimer.reset();
                    mc.executeTask(() ->
                    {
                        this.speed +=
                                this.lastExp * this.multiplier.getValue();

                        this.distance +=
                                this.lastExp * this.multiplier.getValue();

                        if (MovementUtil.getMotionY() > 0
                                && this.vertical.getValue() != 0) {
                            MovementUtil.setMotionY(MovementUtil.getMotionY() * this.vertical.getValue());
                        }
                    });
                }
            }
        } else if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            lagTimer.reset();
            if (mc.player != null) {
                this.distance = 0.0;
            }

            this.speed = 0.0;
            this.stage = 4;
        } else if (event.getPacket() instanceof ExplosionS2CPacket packet) {

            if (this.explosions.getValue()
                    && MovementUtil.isMoving())
            {
                if (mc.player.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) < 200)
                {
                    double speed = Math.sqrt(
                            Math.abs(packet.getPlayerVelocityX() * packet.getPlayerVelocityX())
                                    + Math.abs(packet.getPlayerVelocityZ() * packet.getPlayerVelocityZ()));
                    if (debug.getValue()) CommandManager.sendChatMessage("speed:" + speed + " lastExp:" + lastExp);
                    this.lastExp = this.expTimer
                            .passedMs(this.coolDown.getValueInt())
                            ? speed
                            : (speed - this.lastExp);

                    if (this.lastExp > 0)
                    {
                        if (debug.getValue()) CommandManager.sendChatMessage("boost");
                        this.expTimer.reset();

                            this.speed +=
                                    this.lastExp * this.multiplier.getValue();

                            this.distance +=
                                    this.lastExp * this.multiplier.getValue();

                            if (MovementUtil.getMotionY() > 0) {
                                MovementUtil.setMotionY(MovementUtil.getMotionY() * this.vertical.getValue());
                            }
                    } else {
                        if (debug.getValue()) CommandManager.sendChatMessage("failed boost");
                    }
                }
            }
        }
    }

    @EventHandler
    public void invoke(UpdateWalkingEvent event) {
        if (!MovementUtil.isMoving()) {
            MovementUtil.setMotionX(0);
            MovementUtil.setMotionZ(0);
        }
        this.distance = MovementUtil.getDistance2D();
    }

    @EventHandler
    public void invoke(MoveEvent event) {
        if (!this.inWater.getValue()
                && (mc.player.isTouchingWater())
                || mc.player.isHoldingOntoLadder()) {
            this.stop = true;
            return;
        }

        if (this.stop) {
            this.stop = false;
            return;
        }

        move(event);
    }
    
    private void move(MoveEvent event) {
        if (!MovementUtil.isMoving()) {
            return;
        }

        if (mc.player.checkFallFlying()) return;
        if (!lagTimer.passedMs(this.lagTime.getValueInt())) {
            return;
        }

        if (this.stage == 1 && MovementUtil.isMoving()) {
            this.speed = 1.35 * MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000) - 0.01;
        } else if (this.stage == 2 && mc.player.isOnGround() && MovementUtil.isMoving() && (MovementUtil.isJumping() || this.jump.getValue())) {
            double yMotion = 0.3999 + MovementUtil.getJumpSpeed();
            MovementUtil.setMotionY(yMotion);
            event.setY(yMotion);
            this.speed = this.speed * (this.boost ? 1.6835 : 1.395);
        } else if (this.stage == 3) {
            this.speed = this.distance - 0.66
                    * (this.distance - MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000));

            this.boost = !this.boost;
        } else {
            if ((mc.world.canCollide(null,
                    mc.player
                            .getBoundingBox()
                            .offset(0.0, MovementUtil.getMotionY(), 0.0))
                    || mc.player.collidedSoftly)
                    && this.stage > 0) {
                this.stage = MovementUtil.isMoving() ? 1 : 0;
            }

            this.speed = this.distance - this.distance / 159.0;
        }

        this.speed = Math.min(this.speed, this.getCap());
        this.speed = Math.max(this.speed, MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000));
        double n = MovementUtil.getMoveForward();
        double n2 = MovementUtil.getMoveStrafe();
        double n3 = mc.player.getYaw();
        if (n == 0.0 && n2 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (n != 0.0 && n2 != 0.0) {
            n *= Math.sin(0.7853981633974483);
            n2 *= Math.cos(0.7853981633974483);
        }
        double n4 = this.strafeY.getValue();
        event.setX((n * this.speed * -Math.sin(Math.toRadians(n3)) + n2 * this.speed * Math.cos(Math.toRadians(n3))) * n4);
        event.setZ((n * this.speed * Math.cos(Math.toRadians(n3)) - n2 * this.speed * -Math.sin(Math.toRadians(n3))) * n4);

        if (MovementUtil.isMoving()) {
            this.stage++;
        }
    }

    public double getCap() {
        double ret = cap.getValue();

        if (!scaleCap.getValue()) {
            return ret;
        }

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            int amplifier = mc.player.getActiveStatusEffects().get(StatusEffects.SPEED)
                    .getAmplifier();

            ret *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slow.getValue() && mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            int amplifier = mc.player.getActiveStatusEffects().get(StatusEffects.SLOWNESS)
                    .getAmplifier();

            ret /= 1.0 + 0.2 * (amplifier + 1);
        }

        return ret;
    }
}