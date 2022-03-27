package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.EntityDiedEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    private List<Entity> died = new ArrayList<>();

    @Inject(at = @At("RETURN"), method = "updateEntity")
    private void onUpdateEntity(Entity entityIn, CallbackInfo info) {
        if (!entityIn.isAlive() && !died.contains(entityIn)) {
            EntityDiedEvent ev = new EntityDiedEvent(entityIn, new BlockPos(entityIn.getPosition().getX(), entityIn.getPosition().getY(), entityIn.getPosition().getZ()), entityIn.getEntityWorld());
            MinecraftForge.EVENT_BUS.post(ev);
            died.add(entityIn);
        }
    }

}
