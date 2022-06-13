package net.modificationstation.stationapi.mixin.flattening;

import net.minecraft.level.Level;
import net.minecraft.level.WorldPopulationRegion;
import net.minecraft.level.chunk.Chunk;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.level.BlockStateView;
import net.modificationstation.stationapi.impl.level.HeightLimitView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldPopulationRegion.class)
public class MixinWorldPopulationRegion implements BlockStateView {
    @Shadow private int field_166;
    @Shadow private int field_167;
    @Shadow private Chunk[][] chunks;
    @Shadow private Level level;

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (y >= ((HeightLimitView) level).getBottomY() && y < ((HeightLimitView) level).getTopY()) {
            int var4 = (x >> 4) - this.field_166;
            int var5 = (z >> 4) - this.field_167;
            if (var4 >= 0 && var4 < this.chunks.length && var5 >= 0 && var5 < this.chunks[var4].length) {
                BlockStateView var6 = (BlockStateView) this.chunks[var4][var5];
                return var6 == null ? States.AIR.get() : var6.getBlockState(x & 15, y, z & 15);
            }
        }
        return States.AIR.get();
    }

    @Override
    public BlockState setBlockState(int x, int y, int z, BlockState blockState) {
        if (y >= ((HeightLimitView) level).getBottomY() && y < ((HeightLimitView) level).getTopY()) {
            int var4 = (x >> 4) - this.field_166;
            int var5 = (z >> 4) - this.field_167;
            if (var4 >= 0 && var4 < this.chunks.length && var5 >= 0 && var5 < this.chunks[var4].length) {
                BlockStateView var6 = (BlockStateView) this.chunks[var4][var5];
                if (var6 != null)
                    return var6.setBlockState(x & 15, y, z & 15, blockState);
            }
        }
        return States.AIR.get();
    }
    
    @ModifyConstant(method = {
        "method_142(IIIZ)I",
        "getTileId(III)I",
        "getTileMeta(III)I"
    }, constant = @Constant(intValue = 128))
    private int changeMaxHeight(int value) {
        return ((HeightLimitView) level).getTopY();
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @ModifyConstant(method = {
            "method_142",
            "getTileId(III)I",
            "getTileMeta(III)I"
    }, constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO, ordinal = 0))
    private int changeMinHeightGE(int value) {
        return ((HeightLimitView) level).getBottomY();
    }

    @ModifyConstant(method = {
            "method_142"
    }, constant = @Constant(intValue = 0, ordinal = 5))
    private int changeMinHeight(int value) {
        return ((HeightLimitView) level).getBottomY();
    }
}
