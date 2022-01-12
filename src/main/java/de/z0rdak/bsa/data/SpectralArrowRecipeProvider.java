package de.z0rdak.bsa.data;

import de.z0rdak.bsa.BetterSpectralArrows;
import de.z0rdak.bsa.config.ConfigCondition;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;
import static net.minecraft.advancements.critereon.RecipeUnlockedTrigger.unlocked;

@Mod.EventBusSubscriber(modid = BetterSpectralArrows.MOD_ID)
public class SpectralArrowRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public SpectralArrowRecipeProvider(DataGenerator dataGen) {
        super(dataGen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ResourceLocation spectralArrowRL = new ResourceLocation("minecraft", "spectral_arrow");

        ConditionalRecipe.builder()
                .addCondition(new ConfigCondition(SpectralArrowLightConfigBuilder.GLOW_INK_RECIPE))
                .addRecipe(ShapelessRecipeBuilder
                        .shapeless(Items.SPECTRAL_ARROW)
                        .requires(Items.GLOW_INK_SAC, SpectralArrowLightConfigBuilder.AMOUNT_GLOW_INK_REQUIRED.get())
                        .requires(Items.ARROW)
                        //.group("combat")
                        .unlockedBy("has_item", hasItems(Items.GLOW_INK_SAC))
                        .unlockedBy("has_recipe", unlocked(new ResourceLocation("minecraft:spectral_arrow")))
                        ::save)
                .build(consumer, spectralArrowRL);

        ConditionalRecipe.builder()
                .addCondition(new ConfigCondition(SpectralArrowLightConfigBuilder.GLOW_BERRY_RECIPE))
                .addRecipe(ShapelessRecipeBuilder
                        .shapeless(Items.SPECTRAL_ARROW)
                        .requires(Items.GLOW_BERRIES, SpectralArrowLightConfigBuilder.AMOUNT_GLOW_BERRY_REQUIRED.get())
                        .requires(Items.ARROW)
                        //.group("combat")
                        .unlockedBy("has_recipe", unlocked(new ResourceLocation("minecraft:spectral_arrow")))
                        .unlockedBy("has_item", hasItems(Items.GLOW_BERRIES))
                        ::save)
                .build(consumer, spectralArrowRL);
    }

    @SubscribeEvent
    public static void onDataGeneration(GatherDataEvent event){
        DataGenerator dataGen = event.getGenerator();
        if (event.includeServer()){
            dataGen.addProvider(new SpectralArrowRecipeProvider(dataGen));
        }
    }
}
