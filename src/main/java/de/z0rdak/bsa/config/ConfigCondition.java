package de.z0rdak.bsa.config;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition implements ICondition, IConditionSerializer<ICondition> {

    ForgeConfigSpec.ConfigValue<Boolean> conditionValue;

    public ConfigCondition(ForgeConfigSpec.ConfigValue<Boolean> predicateValue){
        this.conditionValue = predicateValue;
    }

    @Override
    public void write(JsonObject json, ICondition value) {

    }

    @Override
    public ICondition read(JsonObject json) {
        return new ConfigCondition(SpectralArrowLightConfigBuilder.GLOW_INK_RECIPE);
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation("forge", "config_boolean");
    }

    @Override
    public JsonObject getJson(ICondition value) {
        return IConditionSerializer.super.getJson(value);
    }

    @Override
    public boolean test() {
        return conditionValue.get().booleanValue();
    }
}
