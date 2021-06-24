package me.nepnep.potionmod

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectType

class FishingEffect: Effect(
    EffectType.BENEFICIAL,
    0x04b21a
) {
    override fun performEffect(entityLivingBaseIn: LivingEntity, amplifier: Int) {}
    override fun affectEntity(
        source: Entity?,
        indirectSource: Entity?,
        entityLivingBaseIn: LivingEntity,
        amplifier: Int,
        health: Double
    ) {}
}