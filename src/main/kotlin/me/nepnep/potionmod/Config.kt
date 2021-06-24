package me.nepnep.potionmod

import net.minecraftforge.common.ForgeConfigSpec

object Config {
    val config: ForgeConfigSpec

    val hunterRange: ForgeConfigSpec.IntValue

    init {
        val builder = ForgeConfigSpec.Builder()
        hunterRange = builder.comment("Range in blocks for the Hunter effect")
            .defineInRange("hunterRange", 20, 0, Int.MAX_VALUE)
        config = builder.build()
    }
}