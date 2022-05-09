package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.EntityDiedEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientLevel.class)
public class MixinClientWorld {

    private List<Entity> died = new ArrayList<>();

    @Inject(at = @At("RETURN"), method = "tickNonPassenger")
    private void onUpdateEntity(Entity entityIn, CallbackInfo info) {
        if (!entityIn.isAlive() && !died.contains(entityIn)) {
            EntityDiedEvent ev = new EntityDiedEvent(entityIn, new BlockPos(entityIn.blockPosition().getX(), entityIn.blockPosition().getY(), entityIn.blockPosition().getZ()), entityIn.getCommandSenderWorld());
            MinecraftForge.EVENT_BUS.post(ev);
            died.add(entityIn);
        }
    }

}
