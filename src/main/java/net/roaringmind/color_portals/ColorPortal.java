package net.roaringmind.color_portals;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionTypes;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;
import net.roaringmind.color_portals.block.enums.BaseColor;

public class ColorPortal {
  private int id;
  private BlockPos origin;
  private BaseColor color;
  private long age;
  private Identifier dimension;

  private ColorPortal(BlockPos origin, World world, BaseColor color) {
    this.origin = origin;
    this.age = world.getTime();
    this.color = color;
    dimension = world.getDimensionKey().getValue();
    this.id = ColorPortals.portalRegistry.addPortal(this, world);
  }

  public static boolean createColorPortal(World world, BlockPos pos, BaseColor color) {
    BlockEntity e = world.getBlockEntity(pos);
    if (!(e instanceof ColorPortalBaseEntity)) {
      ColorPortals.LOGGER.warn("blockentity in create portal not instance of ColorPortalBaseEntity");
      return false;
    }
    Direction base_direction = world.getBlockState(pos).get(ColorPortalBase.FACING);
    List<BlockPos> portalBlocks = getPortalBlocks(world, pos, base_direction);
    if (portalBlocks == null || portalBlocks.size() == 0) {
      return false;
    }
    ColorPortalBase.setColor(world, pos, color);

    ColorPortal portal = new ColorPortal(pos, world, color);

    for (BlockPos block_pos : portalBlocks) {
      world.setBlockState(block_pos,
          ColorPortals.COLOR_PORTAL_BLOCK.getStateWithRotation(base_direction.getAxis() == Axis.X ? Axis.Z : Axis.X));
      ((ColorPortalBlockEntity) world.getBlockEntity(block_pos)).setPortal(portal.getId());
    }
    ((ColorPortalBaseEntity) e).setPortal(portal.getId());
    return true;
  }

  public BaseColor getColor() {
    return color;
  }

  public BlockPos getPos() {
    return origin;
  }

  public static ColorPortal getById(int id) {
    if (id == -1) {
      return null;
    }
    return ColorPortals.portalRegistry.getById(id);
  }

  public long getAge() {
    return this.age;
  }

  public void destroy(WorldAccess world) {
    ColorPortals.portalRegistry.removePortal(world, this.id);

    if (world.getBlockState(origin).isOf(ColorPortals.COLOR_PORTAL_BASE)) {
      world.setBlockState(origin, world.getBlockState(origin).with(ColorPortalBase.COLOR, BaseColor.NONE),
          Block.NOTIFY_ALL);
    }

    ColorPortals.LOGGER.info("destroyed portal with origin " + origin.toShortString());
  }

  public static int getCost(int id) {

    ColorPortal a = getById(id - id % 2), b = getById(id - (id % 2) + 1);

   

    if (a == null || b == null) {
      return -1;
    }

    double dist = getEuclideanHorizontalDistance(a.origin, b.origin);

    int base_cost = 5;
    double dim_cost_a = a.getDimCost();
    double dim_cost_b = b.getDimCost();
    double dist_cost = Math.max(a.getDistCost(dist), b.getDistCost(dist));

    return (int) (base_cost + dim_cost_a + dim_cost_b + dist_cost);
  }

  private double getDistCost(double dist) {
    int multiplicator = 1;
    if (dimension == DimensionTypes.THE_NETHER_ID) {
      multiplicator = 8;
    }

    return ((dist * multiplicator) / 160) * ((dist * multiplicator) / 160);
  }

  private double getDimCost() {
    if (dimension == DimensionTypes.OVERWORLD_ID) {
      return 0;
    }
    if (dimension == DimensionTypes.THE_NETHER_ID) {
      return 20;
    }

    if (dimension == DimensionTypes.THE_END_ID) {
      return 50 + getDistCost(1024);
    }
    return -1;
  }

  private static double getEuclideanHorizontalDistance(BlockPos a, BlockPos b) {
    int x = Math.abs(a.getX() - b.getX()), y = Math.abs(a.getY() - b.getY());

    return Math.sqrt(x * x + y + y);
  }

  private int getId() {
    return id;
  }

  private static List<BlockPos> getPortalBlocks(World world, BlockPos pos, Direction direction) {
    boolean[][] visited = new boolean[23][23];
    List<BlockPos>[] fillable_blocks = new List[4];

    int x = 0;
    int y = 0;
    visited[x + 12][x + 12] = true;

    int[][] neighbors = {
        { x - 1, y },
        { x + 1, y },
        { x, y + 1 },
        { x, y - 1 }
    };

    List<BlockPos> res = new ArrayList<>();
    for (int i = 0; i < 4; ++i) {
      visited = new boolean[23][23];
      visited[x + 12][x + 12] = true;
      fillable_blocks[i] = new ArrayList<>();
      if (dfs(visited, fillable_blocks[i], neighbors[i][0], neighbors[i][1], world, pos, direction)) {
        res.addAll(fillable_blocks[i]);
      }
    }

    return res;
  }

  private static Boolean dfs(boolean[][] visited, List<BlockPos> fillable_blocks, int x, int y, World world,
      BlockPos pos, Direction direction) {
    if (x > 10 || x < -10 || y > 10 || y < -10) {
      return false;
    }
    if (visited[x + 12][y + 12]) {
      return true;
    }
    visited[x + 12][y + 12] = true;

    BlockPos new_pos = getBlockPosAtRelativeXY(x, y, pos, direction.getAxis());
    Block block = world.getBlockState(new_pos).getBlock();

    if (isBorderBlock(block)) {
      return true;
    }

    if (!(block instanceof AirBlock)) {
      return false;
    }

    int[][] neighbors = {
        { x - 1, y },
        { x + 1, y },
        { x, y + 1 },
        { x, y - 1 }
    };

    for (var neighbor : neighbors) {
      if (!dfs(visited, fillable_blocks, neighbor[0], neighbor[1], world, pos, direction)) {
        return false;
      }
    }

    fillable_blocks.add(new_pos);
    return true;
  }

  private static boolean isBorderBlock(Block block) {
    return block == Blocks.OBSIDIAN;
  }

  private static BlockPos getBlockPosAtRelativeXY(int x, int y, BlockPos pos, Direction.Axis axis) {
    switch (axis) {
      case Z:
        return new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ());
      case X:
        return new BlockPos(pos.getX(), pos.getY() + y, pos.getZ() + x);
      default:
        ColorPortals.LOGGER.warn("couldn't get block pos at relative xy " + x + " " + y + " with origin "
            + pos.toShortString() + ": axis equals Y");
        return null;
    }
  }

  private ColorPortal() {

  }

  public static ColorPortal createFromNbt(NbtCompound tag) {
    if (tag.isEmpty()) {
      return null;
    }

    ColorPortal portal = new ColorPortal();
    portal.id = tag.getInt("id");
    portal.color = BaseColor.byId(tag.getInt("color"));
    portal.age = tag.getLong("age");
    portal.origin = BlockPos.fromLong(tag.getLong("pos"));
    portal.dimension = Identifier.tryParse(tag.getString("dim"));
    return portal;
  }

  public NbtCompound writeNbt() {
    NbtCompound compound = new NbtCompound();
    compound.putInt("id", id);
    compound.putInt("color", color.getId());
    compound.putLong("age", age);
    compound.putLong("pos", origin.asLong());
    compound.putString("dim", dimension.toString());
    return compound;
  }
}
