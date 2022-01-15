package de.z0rdak.bsa.data;

import de.z0rdak.bsa.BetterSpectralArrows;
import de.z0rdak.bsa.handler.SpectralArrowHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = BetterSpectralArrows.MOD_ID)
public class SpectralArrowManager extends SavedData {

    private final Map<ResourceKey<Level>, ArrayList<LightBlockTracker>> trackedLightBlocks = new HashMap<>();
    /* light blocks to remove next interval */
    private final Map<ResourceKey<Level>, ArrayList<LightBlockTracker>> lightBlocksToRemove = new HashMap<>();

    private static final String DATA_NAME = BetterSpectralArrows.MOD_ID;
    private static final String TAG_BSA = "bsa";
    private static SpectralArrowManager instance = new SpectralArrowManager();

    private SpectralArrowManager() {
    }

    public static SpectralArrowManager get() {
        if (instance == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                if (overworld != null && !overworld.isClientSide) {
                    DimensionDataStorage storage = overworld.getDataStorage();
                    instance = storage.computeIfAbsent(SpectralArrowManager::read, SpectralArrowManager::new, DATA_NAME);
                }
            }
        }
        return instance;
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        try {
            ServerLevel world = Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD));
            if (!world.isClientSide) {
                DimensionDataStorage storage = world.getDataStorage();
                SpectralArrowManager data = storage.computeIfAbsent(SpectralArrowManager::read, SpectralArrowManager::new, DATA_NAME);
                storage.set(DATA_NAME, data);
                instance = data;
//                 BetterSpectralArrows.LOGGER.info("Loaded " + data.().size() + " regions for " + data.getDimensionList().size() + " different dimensions");
            }
        } catch (NullPointerException npe) {
            BetterSpectralArrows.LOGGER.error("Loading dimension lights failed");
        }
    }

    public static SpectralArrowManager read(CompoundTag tagCompound) {
        BetterSpectralArrows.LOGGER.info("Reading light data from world...");
        SpectralArrowManager manager = new SpectralArrowManager();
        CompoundTag dims = tagCompound.getCompound(TAG_BSA);
        dims.getAllKeys().forEach(dim -> {
            ResourceKey<Level> dimLevel = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
            manager.init(dimLevel);
            CompoundTag dimCompound = dims.getCompound(dim);
            ListTag lights = dimCompound.getList("lights", ListTag.TAG_COMPOUND);
            lights.forEach(lightTrackerTag -> {
                CompoundTag lightTag = ((CompoundTag) lightTrackerTag);
                CompoundTag posTag = lightTag.getCompound("pos");
                BlockPos pos = new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
                LightBlockTracker lightTracker = new LightBlockTracker(pos, lightTag.getInt("lightLvl"), lightTag.getLong("tickCount"));
                manager.trackedLightBlocks.get(dimLevel).add(lightTracker);
            });
        });
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        CompoundTag dims = new CompoundTag();
        trackedLightBlocks.forEach((dim, lightBlocks) -> {
            CompoundTag dimTag = new CompoundTag();
            ListTag lights = new ListTag();
            lightBlocks.forEach(lightBlock -> {
                CompoundTag lightTracker = new CompoundTag();
                lightTracker.putLong("tickCount", lightBlock.tickCountAtCreation);
                lightTracker.putInt("lightLvl", lightBlock.lightLevel);
                CompoundTag blockPos = new CompoundTag();
                blockPos.putInt("x", lightBlock.pos.getX());
                blockPos.putInt("y", lightBlock.pos.getY());
                blockPos.putInt("z", lightBlock.pos.getZ());
                lightTracker.put("pos", blockPos);
                lights.add(lightTracker);
            });
            dimTag.put("lights", lights);
            dims.put(dim.location().toString(), dimTag);
        });
        BetterSpectralArrows.LOGGER.info("SAVING");
        tagCompound.put(TAG_BSA, dims);
        // TODO: if blocks to remove is not empty save them
        return tagCompound;
    }

    public boolean trackLight(ResourceKey<Level> dim, LightBlockTracker light) {
        if (!trackedLightBlocks.containsKey(dim)) {
            lightBlocksToRemove.put(dim, new ArrayList<>());
            trackedLightBlocks.put(dim, new ArrayList<>());
        }
        boolean res = trackedLightBlocks.get(dim).add(light);
        this.setDirty();
        return res;
    }

    public void init(ResourceKey<Level> dim) {
        trackedLightBlocks.put(dim, new ArrayList<>());
        lightBlocksToRemove.put(dim, new ArrayList<>());
    }

    public void markForRemoval(ResourceKey<Level> dim, LightBlockTracker lightTracker) {
        this.lightBlocksToRemove.get(dim).add(lightTracker);
    }

    public void removeDecaying(long tickCount, int decayIntervalInTicks) {
        trackedLightBlocks.forEach((dim, lightBlocks) -> {
            lightBlocks.forEach(lightBlock -> {
                if (lightBlock.isDecaying(tickCount, decayIntervalInTicks)) {
                    SpectralArrowHandler.reduceLightLevel(dim, lightBlock);
                }
            });
        });
        lightBlocksToRemove.forEach((dim, lightBlocks) -> {
            lightBlocks.forEach(lightBlock -> {
                trackedLightBlocks.get(dim).remove(lightBlock);
            });
        });
        trackedLightBlocks.keySet().forEach(dim -> {
            lightBlocksToRemove.get(dim).clear();
        });
        this.setDirty();
    }
}
