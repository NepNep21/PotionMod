package me.nepnep.potionmod

import net.minecraft.client.Minecraft
import net.minecraft.entity.MobEntity
import net.minecraft.entity.item.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.ItemFishedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import kotlin.math.sqrt

// Does not work in IDE
@Mod(PotionMod.ID)
class PotionMod {
    companion object {
        const val ID = "potionmod"
        val effects: DeferredRegister<Effect> = DeferredRegister.create(ForgeRegistries.POTIONS, ID)
        val potions: DeferredRegister<Potion> = DeferredRegister.create(ForgeRegistries.POTION_TYPES, ID)
        val items: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ID)

        val hunterEffect: RegistryObject<Effect> = effects.register("hunter") { HunterEffect() }
        val hunterPotion: RegistryObject<Potion> = potions.register("hunter_potion") {
            Potion(ID + "_hunter_potion", EffectInstance(hunterEffect.get(), 6000))
        }

        val fishingEffect: RegistryObject<Effect> = effects.register("fishing") { FishingEffect() }
        val fishingPotion: RegistryObject<Potion> = potions.register("fishing_potion") {
            Potion(ID + "_fishing_potion", EffectInstance(fishingEffect.get(), 12000))
        }

        val glowingInk: RegistryObject<Item> = items.register("glowing_ink") { GlowingInkItem() }
    }

    init {
        effects.register(FMLJavaModLoadingContext.get().modEventBus)
        potions.register(FMLJavaModLoadingContext.get().modEventBus)
        items.register(FMLJavaModLoadingContext.get().modEventBus)

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.config)
        FMLJavaModLoadingContext.get().modEventBus.addListener(this::setup)
        MinecraftForge.EVENT_BUS.addListener(this::onItemFished)
    }

    private fun setup(event: FMLCommonSetupEvent) {
        val awkward = Ingredient.fromStacks(PotionUtils.addPotionToItemStack(ItemStack(Items.POTION), Potions.AWKWARD))
        val hunterStack = PotionUtils.addPotionToItemStack(ItemStack(Items.POTION), hunterPotion.get())
        val luck = PotionUtils.addPotionToItemStack(ItemStack(Items.POTION), Potions.LUCK)
        val fishing = PotionUtils.addPotionToItemStack(ItemStack(Items.POTION), fishingPotion.get())
        event.enqueueWork {
            BrewingRecipeRegistry.addRecipe(awkward, Ingredient.fromItems(glowingInk.get()), hunterStack)
            BrewingRecipeRegistry.addRecipe(awkward, Ingredient.fromItems(Items.NAUTILUS_SHELL), luck)
            BrewingRecipeRegistry.addRecipe(awkward, Ingredient.fromItems(Items.TROPICAL_FISH), fishing)
        }
    }

    private fun onItemFished(event: ItemFishedEvent) {
        val entityLiving = event.entityLiving
        if (entityLiving.isPotionActive(fishingEffect.get())) {
            val hookEntity = event.hookEntity
            for (itemStack in event.drops) {
                val itemEntity = ItemEntity(hookEntity.world, hookEntity.posX, hookEntity.posY, hookEntity.posZ, itemStack)
                val diffX = entityLiving.posX - hookEntity.posX
                val diffY = entityLiving.posY - hookEntity.posY
                val diffZ = entityLiving.posZ - hookEntity.posZ
                itemEntity.setMotion(diffX * 0.1,
                    diffY * 0.1 + sqrt(sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ)) * 0.08,
                    diffZ * 0.1)
                hookEntity.world.addEntity(itemEntity)
            }
        }
    }

    @Suppress("UNUSED")
    @Mod.EventBusSubscriber(modid = ID, value = [Dist.CLIENT])
    object ClientEvents {
        @Suppress("UNUSED_PARAMETER")
        @SubscribeEvent
        @JvmStatic
        fun tickEvent(event: TickEvent.ClientTickEvent) {
            val minecraft = Minecraft.getInstance()
            if (minecraft.world != null) {
                for (entity in minecraft.world!!.allEntities) {
                    if (entity is MobEntity) {
                        if (minecraft.player!!.isPotionActive(hunterEffect.get()) && minecraft.player!!.getDistance(entity) <= Config.hunterRange.get()) {
                            entity.isGlowing = true
                        } else {
                            entity.isGlowing = entity.isPotionActive(Effects.GLOWING)
                        }
                    }
                }
            }
        }
    }
}