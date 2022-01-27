package de.z0rdak.bsa;

import de.z0rdak.bsa.config.BooleanConfigCondition;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import de.z0rdak.bsa.data.SpectralArrowManager;
import de.z0rdak.bsa.data.SpectralArrowRecipeProvider;
import de.z0rdak.bsa.handler.SpectralArrowHandler;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BetterSpectralArrows.MOD_ID)

public class BetterSpectralArrows {
    public static final String MOD_ID = "bsa";
    public static final String MOD_NAME = "BetterSpectralArrows";
    public static final Logger LOGGER = LogManager.getLogger();

    /*
     * Seems to work mostly
     * TODOS:
     * - Issue?: Recipe loader returns null instead false on false recipe condition (works but is not like it should be)
     * - Issue: Arrows don't consider non-full blocks light grass, path blocks, etc
     * - Feature: Add flaming arrows setting fire?
     * - Feature: Arrows stick for amount of ticks and de-spawn only after - when picked up, the light block is removed
     * */
    public BetterSpectralArrows() {
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {

            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            modEventBus.addListener(this::setup);

            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(SpectralArrowRecipeProvider.class);
            MinecraftForge.EVENT_BUS.register(SpectralArrowManager.class);
            MinecraftForge.EVENT_BUS.register(SpectralArrowHandler.class);
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SpectralArrowLightConfigBuilder.CONFIG_SPEC, MOD_ID + ".toml");
        });
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LOGGER.info("You are loading " + MOD_NAME + " on a client. " + MOD_NAME + " is a server only mod!"));
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(BooleanConfigCondition.Serializer.INSTANCE);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        CraftingHelper.register(BooleanConfigCondition.Serializer.INSTANCE);
    }
}
