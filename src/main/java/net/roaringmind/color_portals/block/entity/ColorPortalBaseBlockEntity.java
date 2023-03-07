package net.roaringmind.color_portals.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.roaringmind.color_portals.ColorPortalsMod;

public class ColorPortalBaseBlockEntity extends BlockEntity{

  public ColorPortalBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(ColorPortalsMod.COLOR_PORTAL_BASE, pos, state);
  }
  
  
}
