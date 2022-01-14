package de.z0rdak.bsa;

import de.z0rdak.bsa.config.BooleanConfigCondition;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(BetterSpectralArrows.MOD_ID)
public class BetterSpectralArrows
{
    public static final String MOD_ID = "bsa";
    public static final Logger LOGGER = LogManager.getLogger();

    public BetterSpectralArrows() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SpectralArrowLightConfigBuilder.CONFIG_SPEC, MOD_ID + ".arrow.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(BetterSpectralArrows.class);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(BooleanConfigCondition.Serializer.INSTANCE);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        CraftingHelper.register(BooleanConfigCondition.Serializer.INSTANCE);
    }
}
