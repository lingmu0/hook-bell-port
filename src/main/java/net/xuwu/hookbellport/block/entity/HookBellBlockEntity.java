package net.xuwu.hookbellport.block.entity;

import net.xuwu.hookbellport.content.HookBellContent;
import net.xuwu.hookbellport.content.HookBellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.EntityTypeTags;

import java.util.List;

/** Stores the short bell animation and delayed raider call. */
public class HookBellBlockEntity extends BlockEntity {
    private static final double MIN_RANGE = 16.0;
    private static final double MAX_RANGE = 128.0;

    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public Direction clickDirection = Direction.NORTH;
    private List<LivingEntity> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;

    public HookBellBlockEntity(BlockPos pos, BlockState state) {
        super(HookBellContent.HOOK_BELL_ENTITY, pos, state);
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 1) {
            updateEntities();
            resonationTicks = 0;
            clickDirection = Direction.from3DDataValue(data);
            ticks = 0;
            shaking = true;
            return true;
        }
        return super.triggerEvent(id, data);
    }

    private static void tick(Level level, BlockPos pos, BlockState state, HookBellBlockEntity bell) {
        if (bell.shaking) {
            ++bell.ticks;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        2, 0.3, 0.3, 0.3, 0.12);
            }
        }

        if (bell.ticks >= 50) {
            bell.shaking = false;
            bell.ticks = 0;
        }

        if (bell.ticks >= 5 && bell.resonationTicks == 0 && hasEligibleRaider(pos, bell.nearbyEntities)) {
            bell.resonating = true;
            level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 0.5F);
        }

        if (bell.resonating) {
            if (bell.resonationTicks < 40) {
                ++bell.resonationTicks;
            } else {
                if (level instanceof ServerLevel) {
                    teleportRaiders(level, pos, bell.nearbyEntities, bell);
                }
                bell.resonating = false;
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, HookBellBlockEntity bell) {
        tick(level, pos, state, bell);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HookBellBlockEntity bell) {
        tick(level, pos, state, bell);
    }

    public void onHit(Direction direction) {
        clickDirection = direction;
        if (shaking) ticks = 0;
        else shaking = true;
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, direction.get3DDataValue());
        }
    }

    private void updateEntities() {
        if (level == null) return;
        if (level.getGameTime() > lastRingTimestamp + 60L || nearbyEntities == null) {
            lastRingTimestamp = level.getGameTime();
            AABB searchBox = new AABB(worldPosition).inflate(MAX_RANGE);
            nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchBox);
        }
        if (!level.isClientSide) {
            for (LivingEntity living : nearbyEntities) {
                if (living.isAlive() && !living.isRemoved()
                        && worldPosition.closerToCenterThan(living.position(), MAX_RANGE)) {
                    living.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, level.getGameTime());
                }
            }
        }
    }

    private static boolean hasEligibleRaider(BlockPos origin, List<LivingEntity> entities) {
        if (entities == null) return false;
        for (LivingEntity living : entities) {
            if (isRaiderWithinRange(origin, living)) return true;
        }
        return false;
    }

    private static boolean isRaiderVehicle(BlockPos origin, LivingEntity living) {
        return living.isAlive() && !living.isRemoved()
                && living.getFirstPassenger() instanceof LivingEntity rider
                && isRaiderWithinRange(origin, rider);
    }

    private static boolean isRaiderWithinRange(BlockPos origin, LivingEntity living) {
        return living.isAlive() && !living.isRemoved() && isInCallRange(origin, living)
                && living.getType().is(EntityTypeTags.RAIDERS)
                && !living.hasCustomName() && !HookBellConfig.isBlacklisted(living.getType());
    }

    private static boolean isInCallRange(BlockPos origin, LivingEntity living) {
        if (!origin.closerToCenterThan(living.position(), MAX_RANGE)) return false;
        if (!origin.closerToCenterThan(living.position(), MIN_RANGE)) return true;

        Vec3 bellCenter = Vec3.atCenterOf(origin);
        HitResult hit = living.level().clip(new ClipContext(living.getEyePosition(), bellCenter,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));
        // Raiders closer than 16 blocks are only called when the bell is obstructed from view.
        // A clear ray (including one that reaches the bell's own collision shape) means they
        // are close enough already and should not be teleported.
        boolean canSeeBell = hit.getType() == HitResult.Type.MISS
                || (hit instanceof net.minecraft.world.phys.BlockHitResult blockHit
                && blockHit.getBlockPos().equals(origin));
        return !canSeeBell;
    }

    private static void teleportRaiders(Level level, BlockPos origin, List<LivingEntity> entities, HookBellBlockEntity bell) {
        if (entities == null || level.getBlockEntity(origin) != bell) return;
        for (LivingEntity living : entities) {
            if (isRaiderWithinRange(origin, living) || isRaiderVehicle(origin, living)) {
                teleport(living, bell);
            }
        }
    }

    private static void teleport(LivingEntity living, HookBellBlockEntity bell) {
        for (int attempt = 0; attempt < 128; ++attempt) {
            BlockPos pos = bell.worldPosition;
            double x = pos.getX() + (living.getRandom().nextDouble() - 0.5) * 8;
            double y = pos.getY() + living.getRandom().nextInt(16) - 8;
            double z = pos.getZ() + (living.getRandom().nextDouble() - 0.5) * 8;
            if (living.randomTeleport(x, y, z, true)) {
                living.level().gameEvent(GameEvent.TELEPORT, living.position(), GameEvent.Context.of(living));
                living.level().playSound(null, living.xo, living.yo, living.zo, SoundEvents.ENDERMAN_TELEPORT,
                        living.getSoundSource(), 1.0F, 1.0F);
                living.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                return;
            }
        }
    }
}
