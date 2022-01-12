package de.z0rdak.bsa.data;

import net.minecraft.core.BlockPos;

public class LightBlockTracker {

    public final BlockPos pos;
    public int lightLevel;
    public long tickCountAtCreation;

    public LightBlockTracker(BlockPos pos, int lightLevel){
        this.pos = pos;
        this.lightLevel = lightLevel;
    }

    public LightBlockTracker(BlockPos pos, int lightLevel, long tickCount){
        this.pos = pos;
        this.lightLevel = lightLevel;
        this.tickCountAtCreation = tickCount;
    }

    public void reduceLightLevel(int lightLevelReduceAmount) {
        this.lightLevel = this.lightLevel - lightLevelReduceAmount;
        if (this.lightLevel < 0){
            this.lightLevel = 0;
        }
    }

    public boolean isDecaying(long tickCount, int decayInterval){
        return ((tickCount - this.tickCountAtCreation) % decayInterval) == 0;
    }
}
