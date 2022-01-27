package de.z0rdak.bsa.data;

import de.z0rdak.bsa.BetterSpectralArrows;
import de.z0rdak.bsa.config.SpectralArrowLightConfigBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

import static de.z0rdak.bsa.config.BooleanConfigCondition.*;;

public class SpectralArrowRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public SpectralArrowRecipeProvider(DataGenerator dataGen) {
        super(dataGen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ConditionalRecipe.builder()
                .addCondition(ofConfig(SpectralArrowLightConfigBuilder.GLOW_INK_RECIPE))
                .addRecipe(ShapelessRecipeBuilder
                        .shapeless(Items.SPECTRAL_ARROW, 2)
                        .requires(Items.GLOW_INK_SAC, SpectralArrowLightConfigBuilder.AMOUNT_GLOW_INK_REQUIRED.get())
                        .requires(Items.ARROW)
                        .group("combat")
                        //.unlockedBy("has_item", has(Items.GLOW_INK_SAC))
                        //.unlockedBy("has_recipe", unlocked(new ResourceLocation("minecraft:spectral_arrow")))
                        ::save)
                .build(consumer, new ResourceLocation(BetterSpectralArrows.MOD_ID, "spectral_arrow_glow_ink"));

        ConditionalRecipe.builder()
                .addCondition(ofConfig(SpectralArrowLightConfigBuilder.GLOW_BERRY_RECIPE))
                .addRecipe(ShapelessRecipeBuilder
                        .shapeless(Items.SPECTRAL_ARROW, 2)
                        .requires(Items.GLOW_BERRIES, SpectralArrowLightConfigBuilder.AMOUNT_GLOW_BERRY_REQUIRED.get())
                        .requires(Items.ARROW)
                        .group("combat")
                        // TODO: fixme
                        //.unlockedBy("has_recipe", unlocked(new ResourceLocation("minecraft:spectral_arrow")))
                        //.unlockedBy("has_item", has(Items.GLOW_BERRIES))
                        ::save)
                .build(consumer, new ResourceLocation(BetterSpectralArrows.MOD_ID, "spectral_arrow_glow_berries"));
    }

    @SubscribeEvent
     public static void onDataGeneration(GatherDataEvent event){
        CraftingHelper.register(Serializer.INSTANCE);
        DataGenerator dataGen = event.getGenerator();
        if (event.includeServer()){
            dataGen.addProvider(new SpectralArrowRecipeProvider(dataGen));
        }
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CraftingHelper.register(Serializer.INSTANCE);
    }


}
