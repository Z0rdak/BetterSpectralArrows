package de.z0rdak.bsa.config;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BooleanConfigCondition implements ICondition {

    private final ForgeConfigSpec.ConfigValue<Boolean> conditionValue;
    private final boolean value;
    private static final ResourceLocation NAME = new ResourceLocation("bsa", "boolean_config");

    public static BooleanConfigCondition ofConfig(ForgeConfigSpec.ConfigValue<Boolean> configVal){
        return new BooleanConfigCondition(configVal);
    }

    public static BooleanConfigCondition ofConfig(ForgeConfigSpec.ConfigValue<Boolean> configVal, boolean val){
        return new BooleanConfigCondition(configVal, val);
    }

    public BooleanConfigCondition(ForgeConfigSpec.ConfigValue<Boolean> predicateValue) {
        if (predicateValue == null){
            throw new IllegalArgumentException("Config value must not be null");
        }
        this.conditionValue = predicateValue;
        this.value = conditionValue.get();
    }

    public BooleanConfigCondition(ForgeConfigSpec.ConfigValue<Boolean> predicateValue, boolean val) {
        this.value = val;
        this.conditionValue = null;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return this.value;
    }

    public static class Serializer implements IConditionSerializer<BooleanConfigCondition>
    {
        public static final BooleanConfigCondition.Serializer INSTANCE = new BooleanConfigCondition.Serializer();

        @Override
        public void write(JsonObject json, BooleanConfigCondition value) {
            if (value.conditionValue != null){
                json.addProperty("isEnabled", value.conditionValue.get());
                json.addProperty("configName", value.conditionValue.getPath().get(1));
            } else
            {
                json.addProperty("isEnabled", value.value);
            }
        }

        @Override
        public BooleanConfigCondition read(JsonObject json) {
            String configName = GsonHelper.getAsString(json, "configName");
            boolean configValue = GsonHelper.getAsBoolean(json, "isEnabled");
            ForgeConfigSpec.ConfigValue<Boolean> configVal = SpectralArrowLightConfigBuilder.getRecipeConfig(configName);
            return ofConfig(configVal, configValue);
        }

        @Override
        public ResourceLocation getID()
        {
            return BooleanConfigCondition.NAME;
        }
    }
}
