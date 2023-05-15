package net.roaringmind.color_portals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.access.EntityPortalInterface;

@Mixin(Entity.class)
public abstract class EntityPortalMixin implements EntityPortalInterface {

  @Shadow
  public abstract boolean hasPortalCooldown();

  @Shadow
  public abstract void resetPortalCooldown();

  @Shadow
  public World world;

  @Shadow
  protected BlockPos lastNetherPortalPosition;

  @Shadow
  protected int netherPortalTime;

  @Shadow
  protected abstract void tickPortalCooldown();

  @Shadow
  public abstract int getMaxNetherPortalTime();

  @Shadow
  public abstract Entity moveToWorld(ServerWorld destination);

  @Shadow
  public abstract boolean hasVehicle();

  int inColorPortal = -1;

  public void setInColorPortal(BlockPos pos, int portal_id) {
    if (this.hasPortalCooldown()) {
      this.resetPortalCooldown();
      return;
    }
    if (!this.world.isClient && !pos.equals(this.lastNetherPortalPosition)) {
      this.lastNetherPortalPosition = pos.toImmutable();
    }

    inColorPortal = portal_id;
  }

  @Inject(method = "tickPortal()V", at = @At(value = "HEAD"), cancellable = true)
  private void tickColorPortal(CallbackInfo ci) {
    if (!(this.world instanceof ServerWorld)) {
      return;
    }
    int i = this.getMaxNetherPortalTime();
    ServerWorld serverWorld = (ServerWorld) this.world;

    if (this.inColorPortal > -1) {
      MinecraftServer minecraftServer = serverWorld.getServer();

      ServerWorld destination = minecraftServer
          .getWorld(ColorPortals.portalRegistry.getPartnerDimension(this.inColorPortal));

      if (destination != null && !this.hasVehicle()
          && this.netherPortalTime++ >= i) {
        this.world.getProfiler().push("portal");
        this.netherPortalTime = i;
        this.resetPortalCooldown();
        this.moveToWorld(destination);
        this.world.getProfiler().pop();
      }
      this.inColorPortal = -1;
      this.tickPortalCooldown();
      ci.cancel();
    }
  }

  @Inject(method = "getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", at = @At(value = "HEAD"), cancellable = true)
  protected void getColorPortalTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
    if (this.inColorPortal > -1) {
      cir.setReturnValue(
          ColorPortals.portalRegistry.getTeleportTarget(destination, inColorPortal, ((Entity) (Object) this)));
    }
  }
}
