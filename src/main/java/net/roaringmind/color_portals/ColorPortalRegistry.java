package net.roaringmind.color_portals;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.block.ColorPortalBlock;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;
import net.roaringmind.color_portals.block.enums.BaseColor;

public class ColorPortalRegistry extends PersistentState {
  private ColorPortal[] list;
  private Boolean[] links;

  public ColorPortalRegistry() {
    list = new ColorPortal[32];
    links = new Boolean[16];
    for (int i = 0; i < 32; ++i) {
      list[i] = null;
      if (i >= 16) {
        continue;
      }
      links[i] = false;
    }
  }

  public void removePortal(WorldAccess world, int id) {
    markDirty();

    if (links[idToColorId(id)]) {
      unlink(world, id);
    }
    list[id] = null;
  }

  private static void baseToBlock(World world, BlockPos pos) {
    Direction base_direction = world.getBlockState(pos).get(ColorPortalBase.FACING);
    BaseColor color = world.getBlockState(pos).get(ColorPortalBase.COLOR);
    world.setBlockState(pos, ColorPortals.COLOR_PORTAL_BLOCK
        .getColoredStateWithRotation(base_direction.getAxis() == Axis.X ? Axis.Z : Axis.X, color));
    ((ColorPortalBlockEntity) world.getBlockEntity(pos)).setBase();
  }

  private void blockToBase(WorldAccess world, int portal_id) {
    BlockPos pos = list[portal_id].getPos();

    Direction base_direction = Direction.get(AxisDirection.POSITIVE,
        world.getBlockState(pos).get(ColorPortalBlock.AXIS));
    world.setBlockState(pos, ColorPortals.COLOR_PORTAL_BASE.getDefaultState()
        .with(ColorPortalBase.FACING, base_direction)
        .with(ColorPortalBase.COLOR, BaseColor.byId(idToColorId(portal_id))), Block.NOTIFY_ALL);
    ((ColorPortalBaseEntity) world.getBlockEntity(pos)).setPortal(portal_id);
  }

  private int getPartnerId(int id) {
    return id % 2 == 1 ? id - 1 : id + 1;
  }

  public RegistryKey<World> getPartnerDimension(int id) {
    int partner_id = getPartnerId(id);

    return list[partner_id].getDimension();
  }

  public TeleportTarget getTeleportTarget(int id, World startworld, World endworld, float yaw, float pitch, Vec3d velocity) {
    int partner_id = getPartnerId(id);

    boolean turn = startworld.getBlockState(list[id].getPos()).get(Properties.HORIZONTAL_AXIS) != endworld
        .getBlockState(list[partner_id].getPos()).get(Properties.HORIZONTAL_AXIS);

    return list[partner_id].getTeleportSpawn(endworld, turn, yaw, pitch, velocity);
  }

  public boolean linkPortal(World world, BlockPos pos) {
    int id = ((ColorPortalBaseEntity) world.getBlockEntity(pos)).getPortal();
    int pair_id = getPair(id);

    if (list[id] == null || list[pair_id] == null) {
      return false;
    }

    BlockPos pos_a = list[id].getPos(), pos_b = list[pair_id].getPos();

    baseToBlock(world, pos_a);
    baseToBlock(world, pos_b);

    links[(id) / 2] = true;
    markDirty();
    return true;
  }

  public int addPortal(ColorPortal portal, World world) {
    markDirty();
    int color_id = portal.getColor().getId();

    if (color_id > 15) {
      ColorPortals.LOGGER.warn("tried adding portal with color id > 15 to portal list");
      return -1;
    }

    int id_a = color_id * 2, id_b = id_a + 1;
    ColorPortal a = list[id_a], b = list[id_b];
    if (a == null) {
      list[id_a] = portal;
      return id_a;
    }
    if (b == null) {
      list[id_b] = portal;
      return id_b;
    }
    if (a.getAge() < b.getAge()) {
      a.destroy(world);
      list[id_a] = portal;
      return id_a;
    }
    b.destroy(world);
    list[id_b] = portal;
    return id_b;
  }

  public ColorPortal getById(int id) {
    return list[id];
  }

  public static ColorPortalRegistry createFromNbt(NbtCompound tag) {
    NbtCompound compound = tag.getCompound(ColorPortals.MODID);
    NbtCompound portals = compound.getCompound("portals");
    NbtCompound linkCompound = compound.getCompound("links");

    ColorPortalRegistry res = new ColorPortalRegistry();
    for (int i = 0; i < 32; ++i) {
      res.list[i] = ColorPortal.createFromNbt(portals.getCompound(String.valueOf(i)));
    }
    for (int i = 0; i < 16; ++i) {
      res.links[i] = linkCompound.getBoolean(String.valueOf(i));
    }
    return res;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound var1) {
    NbtCompound compound = new NbtCompound();
    NbtCompound portals = new NbtCompound();
    for (int i = 0; i < 32; ++i) {
      NbtCompound portal_compound = new NbtCompound();
      if (list[i] != null) {
        portal_compound = list[i].writeNbt();
      }

      portals.put(String.valueOf(i), portal_compound);
    }

    NbtCompound linkCompound = new NbtCompound();
    for (int i = 0; i < 16; ++i) {
      linkCompound.putBoolean(String.valueOf(i), links[i]);
    }

    compound.put("portals", portals);
    compound.put("links", linkCompound);
    var1.put(ColorPortals.MODID, compound);
    return var1;
  }

  private void unlink(WorldAccess world, int id) {
    links[idToColorId(id)] = false;

    blockToBase(world, id);
    blockToBase(world, getPair(id));
  }

  private static int getPair(int id) {
    return id % 2 == 0 ? id + 1 : id - 1;
  }

  private static int idToColorId(int id) {
    return (id - id % 2) / 2;
  }
}