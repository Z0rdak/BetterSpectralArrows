package de.z0rdak.bsa;

import de.z0rdak.bsa.config.ConfigCondition;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import de.z0rdak.bsa.data.SpectralArrowManager;
import de.z0rdak.bsa.data.SpectralArrowRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


@Mod(BetterSpectralArrows.MOD_ID)
public class BetterSpectralArrows
{
    public static final String MOD_ID = "bsa";
    public static final Logger LOGGER = LogManager.getLogger();

    public BetterSpectralArrows() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SpectralArrowLightConfigBuilder.CONFIG_SPEC, MOD_ID + ".arrow.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        CraftingHelper.register(new ConfigCondition(SpectralArrowLightConfigBuilder.GLOW_BERRY_RECIPE));
        CraftingHelper.register(new ConfigCondition(SpectralArrowLightConfigBuilder.GLOW_INK_RECIPE));
    }


}
