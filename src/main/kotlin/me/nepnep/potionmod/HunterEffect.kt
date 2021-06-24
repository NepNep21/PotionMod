package me.nepnep.potionmod

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectType

class HunterEffect: Effect(
    EffectType.BENEFICIAL,
    0xff8900
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