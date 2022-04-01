package de.keksuccino.cinematica.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    @Nullable
    public static String getCurrentDimensionKey() {
        try {
            if (Minecraft.getInstance().world != null) {
                String ns = Minecraft.getInstance().player.world.getDimensionKey().getLocation().getNamespace();
                String path = Minecraft.getInstance().player.world.getDimensionKey().getLocation().getPath();
                return ns + ":" + path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Entity> getEntitiesInPlayerFOV(List<Entity> entitiesToCheck, double fovOffset) {
        List<Entity> entities = new ArrayList<>();
        try {
            EntityRendererManager rm = Minecraft.getInstance().getRenderManager();
            GameRenderer gr = Minecraft.getInstance().gameRenderer;
            if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null) && (rm != null) && (gr != null)) {
                ActiveRenderInfo activeRenderInfo = gr.getActiveRenderInfo();
                if (activeRenderInfo != null) {
                    Vector3d vector3d = activeRenderInfo.getProjectedView();
                    double d0 = vector3d.getX();
                    double d1 = vector3d.getY();
                    double d2 = vector3d.getZ();
                    MatrixStack matrixStack = new MatrixStack();
                    matrixStack.rotate(Vector3f.ZP.rotationDegrees(0));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(activeRenderInfo.getPitch()));
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(activeRenderInfo.getYaw() + 180.0F));
                    MatrixStack matrixStack2 = new MatrixStack();
                    matrixStack2.getLast().getMatrix().mul(getProjectionMatrix(activeRenderInfo, Minecraft.getInstance().getRenderPartialTicks(), true, fovOffset));
                    ClippingHelper clippingHelper = new ClippingHelper(matrixStack.getLast().getMatrix(), matrixStack2.getLast().getMatrix());
                    clippingHelper.setCameraPosition(d0, d1, d2);
                    for (Entity e : entitiesToCheck) {
                        if (Minecraft.getInstance().getRenderManager().shouldRender(e, clippingHelper, d0, d1, d2)) {
                            entities.add(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    public static List<Entity> getEntitiesInPlayerFOV(double distance, double fovOffset) {
        return getEntitiesInPlayerFOV(getEntitiesAroundPlayer(distance), fovOffset);
    }

    public static List<Entity> getEntitiesAroundPlayer(double radius) {
        List<Entity> entities = new ArrayList<>();
        try {
            if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null)) {
                AxisAlignedBB rangeBB = new AxisAlignedBB(Minecraft.getInstance().player.getPosition(), Minecraft.getInstance().player.getPosition());
                rangeBB = rangeBB.grow(radius);
                List<Entity> entitiesInRange = Minecraft.getInstance().world.getEntitiesWithinAABBExcludingEntity(Minecraft.getInstance().player, rangeBB);
                if (entitiesInRange != null) {
                    entities = entitiesInRange;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    protected static Matrix4f getProjectionMatrix(ActiveRenderInfo activeRenderInfoIn, float partialTicks, boolean useFovSetting, double fovOffset) {
        float farPlaneDistance = (float)(Minecraft.getInstance().gameSettings.renderDistanceChunks * 16);
        float cameraZoom = 1.0F;
        float cameraYaw = 0;
        float cameraPitch = 0;
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.getLast().getMatrix().setIdentity();
        if (cameraZoom != 1.0F) {
            matrixstack.translate(cameraYaw, (-cameraPitch), 0.0D);
            matrixstack.scale(cameraZoom, cameraZoom, 1.0F);
        }
        matrixstack.getLast().getMatrix().mul(Matrix4f.perspective(getFOVModifier(activeRenderInfoIn, partialTicks, useFovSetting, fovOffset), (float)Minecraft.getInstance().getMainWindow().getFramebufferWidth() / (float)Minecraft.getInstance().getMainWindow().getFramebufferHeight(), 0.05F, farPlaneDistance * 4.0F));
        return matrixstack.getLast().getMatrix();
    }

    protected static double getFOVModifier(ActiveRenderInfo activeRenderInfoIn, float partialTicks, boolean useFOVSetting, double fovOffset) {
        boolean debugView = false;
        if (debugView) {
            return 90.0D;
        } else {
            double fov = 70.0D;
            if (useFOVSetting) {
                fov = Minecraft.getInstance().gameSettings.fov + fovOffset;
                fov = fov * (double) MathHelper.lerp(partialTicks, getGameRendererFovModifierHandPrev(), getGameRendererFovModifierHand());
            }
            if (activeRenderInfoIn.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).getShouldBeDead()) {
                float f = Math.min((float)((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).deathTime + partialTicks, 20.0F);
                fov /= ((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
            }
            FluidState fluidstate = activeRenderInfoIn.getFluidState();
            if (!fluidstate.isEmpty()) {
                fov = fov * 60.0D / 70.0D;
            }
            return fov;
        }
    }

    protected static float getGameRendererFovModifierHand() {
        try {
            Field f = ObfuscationReflectionHelper.findField(GameRenderer.class, "field_78507_R"); //fovModifierHand
            return (float) f.get(Minecraft.getInstance().gameRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0F;
    }

    protected static float getGameRendererFovModifierHandPrev() {
        try {
            Field f = ObfuscationReflectionHelper.findField(GameRenderer.class, "field_78506_S"); //fovModifierHandPrev
            return (float) f.get(Minecraft.getInstance().gameRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0F;
    }

}
