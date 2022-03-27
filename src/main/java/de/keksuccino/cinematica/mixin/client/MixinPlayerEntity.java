package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.MixinHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

//    @Inject(at = @At("HEAD"), method = "attackTargetEntityWithCurrentItem")
//    private void onAttackTargetEntityWithCurrentItem(Entity target, CallbackInfo info) {
//        PlayerEntity attacker = (PlayerEntity)((Object)this);
//        if (target instanceof LivingEntity) {
//            if (attacker.getUniqueID().toString().equals(Minecraft.getInstance().player.getUniqueID().toString())) {
//                MixinHandler.lastAttackedLivingEntity = (LivingEntity) target;
//            }
//        }
//    }

}
