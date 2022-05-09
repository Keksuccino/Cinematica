package de.keksuccino.cinematica.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    @Nullable
    public static String getCurrentDimensionKey() {
        try {
            if (Minecraft.getInstance().level != null) {
                String ns = Minecraft.getInstance().player.level.dimension().location().getNamespace();
                String path = Minecraft.getInstance().player.level.dimension().location().getPath();
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
            EntityRenderDispatcher rm = Minecraft.getInstance().getEntityRenderDispatcher();
            GameRenderer gr = Minecraft.getInstance().gameRenderer;
            if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().level != null) && (rm != null) && (gr != null)) {
                Camera activeRenderInfo = gr.getMainCamera();
                if (activeRenderInfo != null) {
                    Vec3 vector3d = activeRenderInfo.getPosition();
                    double d0 = vector3d.x();
                    double d1 = vector3d.y();
                    double d2 = vector3d.z();
                    PoseStack PoseStack = new PoseStack();
                    PoseStack.mulPose(Vector3f.ZP.rotationDegrees(0));
                    PoseStack.mulPose(Vector3f.XP.rotationDegrees(activeRenderInfo.getXRot()));
                    PoseStack.mulPose(Vector3f.YP.rotationDegrees(activeRenderInfo.getYRot() + 180.0F));
                    PoseStack PoseStack2 = new PoseStack();
                    PoseStack2.last().pose().multiply(getProjectionMatrix(activeRenderInfo, Minecraft.getInstance().getFrameTime(), true, fovOffset));
                    Frustum clippingHelper = new Frustum(PoseStack.last().pose(), PoseStack2.last().pose());
                    clippingHelper.prepare(d0, d1, d2);
                    for (Entity e : entitiesToCheck) {
                        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRender(e, clippingHelper, d0, d1, d2)) {
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
            if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().level != null)) {
                AABB rangeBB = new AABB(Minecraft.getInstance().player.blockPosition(), Minecraft.getInstance().player.blockPosition());
                rangeBB = rangeBB.inflate(radius);
                List<Entity> entitiesInRange = Minecraft.getInstance().level.getEntities(Minecraft.getInstance().player, rangeBB);
                if (entitiesInRange != null) {
                    entities = entitiesInRange;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    public static String getCurrentBiomeName() {
        try {
            BlockPos blockPos = Minecraft.getInstance().getCameraEntity().blockPosition();
            Holder<Biome> biomeHolder = Minecraft.getInstance().level.getBiome(blockPos);
            return biomeHolder.unwrap().map((p_205377_) -> {
                return p_205377_.location().toString();
            }, (p_205367_) -> {
                return "[unregistered " + p_205367_ + "]";
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[unregistered]";
    }

    protected static Matrix4f getProjectionMatrix(Camera activeRenderInfoIn, float partialTicks, boolean useFovSetting, double fovOffset) {
        float farPlaneDistance = (float)(Minecraft.getInstance().options.renderDistance * 16);
        float cameraZoom = 1.0F;
        float cameraYaw = 0;
        float cameraPitch = 0;
        PoseStack PoseStack = new PoseStack();
        PoseStack.last().pose().setIdentity();
        if (cameraZoom != 1.0F) {
            PoseStack.translate(cameraYaw, (-cameraPitch), 0.0D);
            PoseStack.scale(cameraZoom, cameraZoom, 1.0F);
        }
        PoseStack.last().pose().multiply(Matrix4f.perspective(getFOVModifier(activeRenderInfoIn, partialTicks, useFovSetting, fovOffset), (float)Minecraft.getInstance().getWindow().getWidth() / (float)Minecraft.getInstance().getWindow().getHeight(), 0.05F, farPlaneDistance * 4.0F));
        return PoseStack.last().pose();
    }

    protected static double getFOVModifier(Camera activeRenderInfoIn, float partialTicks, boolean useFOVSetting, double fovOffset) {
        boolean debugView = false;
        if (debugView) {
            return 90.0D;
        } else {
            double fov = 70.0D;
            if (useFOVSetting) {
                fov = Minecraft.getInstance().options.fov + fovOffset;
                fov = fov * (double) Mth.lerp(partialTicks, getGameRendererFovModifierHandPrev(), getGameRendererFovModifierHand());
            }
            if (activeRenderInfoIn.getEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getEntity()).isDeadOrDying()) {
                float f = Math.min((float)((LivingEntity)activeRenderInfoIn.getEntity()).deathTime + partialTicks, 20.0F);
                fov /= ((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
            }
            FogType fogtype = activeRenderInfoIn.getFluidInCamera();
            if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
                fov *= Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0F, 0.85714287F);
            }
            return fov;
        }
    }

    protected static float getGameRendererFovModifierHand() {
        try {
            Field f = ObfuscationReflectionHelper.findField(GameRenderer.class, "f_109066_"); //fov
            return (float) f.get(Minecraft.getInstance().gameRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0F;
    }

    protected static float getGameRendererFovModifierHandPrev() {
        try {
            Field f = ObfuscationReflectionHelper.findField(GameRenderer.class, "f_109067_"); //oldFov
            return (float) f.get(Minecraft.getInstance().gameRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0F;
    }

}
