package de.z0rdak.bsa.handler;

import de.z0rdak.bsa.BetterSpectralArrows;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import de.z0rdak.bsa.data.LightBlockTracker;
import de.z0rdak.bsa.data.SpectralArrowManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class SpectralArrowHandler {

    private static MinecraftServer server;
    private static final IntegerProperty lightProperty = IntegerProperty.create("level", 0, 15);
    private static final BooleanProperty waterlogged = BooleanProperty.create("waterlogged");

    @SubscribeEvent
    public static void onSpectralArrowHitBlock(ProjectileImpactEvent event) {
        if (!event.getEntity().getCommandSenderWorld().isClientSide) {
            BlockPos hit = new BlockPos(event.getRayTraceResult().getLocation());
            BlockPos arrow = new BlockPos(event.getProjectile().position());
            // check if impact is on block and projectile is spectral arrow
            // TODO: add check for wasOnFire and normal arrow or abstractarrow
            if (event.getEntity() instanceof SpectralArrow arrowEntity && event.getRayTraceResult().getType() == HitResult.Type.BLOCK) {
                BlockPos lightBlockPos;
                BlockPos diff = hit.subtract(arrow);
                // UP and DOWN are not caught with getDirection()
                if (Math.abs(diff.getY()) > 0 && (diff.getZ() == 0 && diff.getX() == 0)) {
                    if (diff.getY() > 0) {
                        lightBlockPos = hit.offset(0, -1, 0);
                    } else {
                        lightBlockPos = hit.offset(0, 0, 0);
                    }
                } else {
                    lightBlockPos = switch (arrowEntity.getDirection()) {
                        case SOUTH -> hit.north();
                        case WEST -> hit.west();
                        /* for some reason arrows fired into other directions have other hit coordinates (1 block off) */
                        default -> hit;
                    };
                }

                Level level = event.getEntity().getCommandSenderWorld();
                BlockState blockToReplace = level.getBlockState(lightBlockPos);
                if (!blockToReplace.is(Blocks.AIR) && !blockToReplace.is(Blocks.WATER)) {
                    BetterSpectralArrows.LOGGER.debug("Try to reduce light level for block != minecraft:light at " + lightBlockPos.toShortString());
                    return;
                }
                boolean wasPlacedSuccessfully = setLightWithLevelAt(level, lightBlockPos, SpectralArrowLightConfigBuilder.START_LIGHT_LVL.get());
                boolean discardArrow = SpectralArrowLightConfigBuilder.DISCARD_ARROW.get();
                if (wasPlacedSuccessfully && discardArrow) {
                    arrowEntity.discard();
                }
                trackLightBlock(level.dimension(), lightBlockPos);
            }
        }
    }

    public static void trackLightBlock(ResourceKey<Level> dim, BlockPos pos) {
        long tickCount = server.getTickCount() + SpectralArrowLightConfigBuilder.LIGHT_DECAY_INTERVAL.get() - 1;
        LightBlockTracker lightBlockTracker = new LightBlockTracker(pos, SpectralArrowLightConfigBuilder.START_LIGHT_LVL.get(), tickCount);
        SpectralArrowManager.get().trackLight(dim, lightBlockTracker);
    }

    public static BlockState createLightLevelState(int level) {
        return Blocks.LIGHT.defaultBlockState().setValue(lightProperty, level);
    }

    public static boolean setLightWithLevelAt(Level world, BlockPos pos, int lightLvl) {
        BlockState lightBlockState = createLightLevelState(lightLvl);
        BlockState blockToReplace = world.getBlockState(pos);
        IntegerProperty waterLevelProperty = IntegerProperty.create("level", 0, 15);
        if (blockToReplace.is(Blocks.WATER) && blockToReplace.getValue(waterLevelProperty) == 0) {
            // set waterlogged state if block is water && is source block
            lightBlockState = lightBlockState.setValue(waterlogged, true);
            return world.setBlockAndUpdate(pos, lightBlockState);
        }
        if (world.getBlockState(pos).is(Blocks.AIR)) {
            return world.setBlockAndUpdate(pos, lightBlockState);
        }
        return false;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        server = event.getServer();
    }

    public static void reduceLightLevel(ResourceKey<Level> dim, LightBlockTracker lightTracker) {
        Level level = server.getLevel(dim);
        if (level != null) {
            int reduceAmount = SpectralArrowLightConfigBuilder.LIGHT_DECAY_STEP.get();
            double decayChance = SpectralArrowLightConfigBuilder.LIGHT_DECAY_CHANCE.get();
            double threshold = ThreadLocalRandom.current().nextDouble();
            if (decayChance < threshold) {
                return;
            }
            lightTracker.reduceLightLevel(reduceAmount);
            level.removeBlock(lightTracker.pos, true);
            setLightWithLevelAt(level, lightTracker.pos, lightTracker.lightLevel);
            BlockState newLightState = level.getBlockState(lightTracker.pos);
            if (isOut(lightTracker)) {
                if (newLightState.hasProperty(waterlogged)) {
                    level.setBlockAndUpdate(lightTracker.pos, newLightState.getValue(waterlogged)
                            ? Blocks.WATER.defaultBlockState()
                            : Blocks.AIR.defaultBlockState());
                }
                SpectralArrowManager.get().markForRemoval(dim, lightTracker);
            }
        }
    }

    @SubscribeEvent
    public static void onSpectralArrowDecay(TickEvent.ServerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            long tickCount = server.getTickCount();
            int decayIntervalInTicks = SpectralArrowLightConfigBuilder.LIGHT_DECAY_INTERVAL.get();

            if (decayIntervalInTicks > 0) {
                SpectralArrowManager.get().removeDecaying(tickCount, decayIntervalInTicks);
            }
        }
    }

    private static boolean isOut(LightBlockTracker lightBlock) {
        return lightBlock.lightLevel <= 0;
    }
}
