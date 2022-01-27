package de.z0rdak.bsa.config;

import de.z0rdak.bsa.BetterSpectralArrows;
import net.minecraftforge.common.ForgeConfigSpec;

public final class SpectralArrowLightConfigBuilder {

    private SpectralArrowLightConfigBuilder(){}

    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> START_LIGHT_LVL;
    public static final ForgeConfigSpec.ConfigValue<Integer> LIGHT_DECAY_INTERVAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> LIGHT_DECAY_STEP;
    public static final ForgeConfigSpec.ConfigValue<Double> LIGHT_DECAY_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISCARD_ARROW;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GLOW_INK_RECIPE;
    public static final ForgeConfigSpec.ConfigValue<Integer> AMOUNT_GLOW_INK_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GLOW_BERRY_RECIPE;
    public static final ForgeConfigSpec.ConfigValue<Integer> AMOUNT_GLOW_BERRY_REQUIRED;

    static {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("BetterSpectralArrows - light block config").build();

        START_LIGHT_LVL = BUILDER.comment("Start light level for generated light blocks.")
                .translation("config.light.level")
                .defineInRange("start_light_lvl", 15, 1, 15);

        LIGHT_DECAY_INTERVAL = BUILDER.comment("Time interval in ticks for decreasing light level of the placed light blocks.\n -1 indicates forever")
                .translation("config.light.decay.interval")
                .defineInRange("decay_interval", 20, -1, Integer.MAX_VALUE);

        LIGHT_DECAY_STEP = BUILDER.comment("Amount of levels to for decreasing the light level of placed light blocks.")
                .translation("config.light.decay.step")
                .defineInRange("decay_amount", 1, 1, 15);

        LIGHT_DECAY_CHANCE = BUILDER.comment("Chance for a light block to decay/reduce its light level.\n 1 indicates 100% decaying chance\n 0 indicates no decaying")
                .translation("config.light.decay.chance")
                .defineInRange("decay_chance", 1.0, 0.0, 1.0);

        BUILDER.pop();
        BUILDER.push("BetterSpectralArrows - Recipe config");

        GLOW_INK_RECIPE = BUILDER.comment("Enable or disable crafting spectral arrows with glow ink")
                .translation("config.crafting.arrow.glow-ink")
                .define("craft_with_glow_ink", true);

        AMOUNT_GLOW_INK_REQUIRED = BUILDER.comment("Amount of glow ink required for recipe")
                .translation("config.crafting.arrow.glow-ink.amount")
                .defineInRange("amount_glow_ink", 1, 1, 8);

        GLOW_BERRY_RECIPE = BUILDER.comment("Enable or disable crafting spectral arrows with glow berries")
                .translation("config.crafting.arrow.glow-berries")
                .define("craft_with_glow_berries", true);

        AMOUNT_GLOW_BERRY_REQUIRED = BUILDER.comment("Amount of glow berries required for recipe")
                .translation("config.crafting.arrow.glow-berries.amount")
                .defineInRange("amount_glow_berries", 2, 1, 8);

        BUILDER.pop();
        BUILDER.push("BetterSpectralArrows - Misc config");

        DISCARD_ARROW = BUILDER.comment("Discard arrow after light block created")
                .translation("config.light.discard")
                .define("discard_arrows", true);

        CONFIG_SPEC = BUILDER.build();
    }

    public static ForgeConfigSpec.ConfigValue<Boolean> getRecipeConfig(String configName){
        return switch (configName) {
            case "craft_with_glow_ink" -> GLOW_INK_RECIPE;
            case "craft_with_glow_berries" -> GLOW_BERRY_RECIPE;
            default -> null;
        };
    }

}
