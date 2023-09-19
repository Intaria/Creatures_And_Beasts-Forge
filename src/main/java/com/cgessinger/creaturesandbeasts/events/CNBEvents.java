package com.cgessinger.creaturesandbeasts.events;

import com.cgessinger.creaturesandbeasts.CreaturesAndBeasts;
import com.cgessinger.creaturesandbeasts.config.CNBConfig;
import com.cgessinger.creaturesandbeasts.entities.CactemEntity;
import com.cgessinger.creaturesandbeasts.entities.EndWhaleEntity;
import com.cgessinger.creaturesandbeasts.entities.LilytadEntity;
import com.cgessinger.creaturesandbeasts.entities.LittleGrebeEntity;
import com.cgessinger.creaturesandbeasts.entities.LizardEntity;
import com.cgessinger.creaturesandbeasts.entities.MinipadEntity;
import com.cgessinger.creaturesandbeasts.entities.SporelingEntity;
import com.cgessinger.creaturesandbeasts.entities.ThrownCactemSpearEntity;
import com.cgessinger.creaturesandbeasts.entities.YetiEntity;
import com.cgessinger.creaturesandbeasts.init.CNBEntityTypes;
import com.cgessinger.creaturesandbeasts.init.CNBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = CreaturesAndBeasts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CNBEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(CNBEntityTypes.SPORELING.get(), SporelingEntity.createAttributes().build());
        event.put(CNBEntityTypes.LITTLE_GREBE.get(), LittleGrebeEntity.createAttributes().build());
        event.put(CNBEntityTypes.LILYTAD.get(), LilytadEntity.createAttributes().build());
        event.put(CNBEntityTypes.LIZARD.get(), LizardEntity.createAttributes().build());
        event.put(CNBEntityTypes.YETI.get(), YetiEntity.createAttributes().build());
        event.put(CNBEntityTypes.MINIPAD.get(), MinipadEntity.createAttributes().build());
        event.put(CNBEntityTypes.END_WHALE.get(), EndWhaleEntity.createAttributes().build());
        event.put(CNBEntityTypes.CACTEM.get(), CactemEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.isSecondaryUseActive() && player.getFirstPassenger() instanceof SporelingEntity sporelingEntity) {
            sporelingEntity.stopRiding();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLootingCalculate(LootingLevelEvent event) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource instanceof IndirectEntityDamageSource indirectDamage && indirectDamage.getDirectEntity() instanceof ThrownCactemSpearEntity thrownSpear) {
            event.setLootingLevel(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, thrownSpear.getSpear()));
        }
    }

	@SubscribeEvent
	public void onItemAttributeModifierCalculate(ItemAttributeModifierEvent event) {
		ItemStack input = event.getItemStack();
        CompoundTag tag = input.getTag();
        EquipmentSlot equipmentSlot = null;
        if (input.getItem() instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) input.getItem();
            equipmentSlot = armorItem.getSlot();
        }

		if (equipmentSlot != null && tag != null && event.getSlotType().equals(equipmentSlot) && tag.contains("HideAmount")) {
            int hideAmount = tag.getInt("HideAmount");

            if (equipmentSlot.equals(EquipmentSlot.HEAD)) {
                event.addModifier(Attributes.ARMOR, new AttributeModifier(UUID.fromString("96a6b318-81f1-475a-b4a4-b3da41d2711e"), "yeti_hide", CNBConfig.hideMultiplier * hideAmount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            } else if (equipmentSlot.equals(EquipmentSlot.CHEST)) {
                event.addModifier(Attributes.ARMOR, new AttributeModifier(UUID.fromString("3f3136ff-4f04-4d62-a9cc-8d1f4175c1e2"), "yeti_hide", CNBConfig.hideMultiplier * hideAmount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            } else if (equipmentSlot.equals(EquipmentSlot.LEGS)) {
                event.addModifier(Attributes.ARMOR, new AttributeModifier(UUID.fromString("f49d078c-2740-4283-8255-5d1f106efea0"), "yeti_hide", CNBConfig.hideMultiplier * hideAmount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            } else {
                event.addModifier(Attributes.ARMOR, new AttributeModifier(UUID.fromString("b16e7c3f-508d-461d-8868-de6ee2a1314c"), "yeti_hide", CNBConfig.hideMultiplier * hideAmount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
		}
	}

    @SubscribeEvent
    public void onAnvilChange(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof ArmorItem && event.getRight().is(CNBItems.YETI_HIDE.get())) {
            ItemStack output = event.getLeft().copy();
            CompoundTag nbt = output.getOrCreateTag();
            int hideAmount = 1;

            if (nbt.contains("HideAmount")) {
                hideAmount += nbt.getInt("HideAmount");

                if (hideAmount > CNBConfig.hideAmount) {
                    return;
                }
            }

            nbt.putInt("HideAmount", hideAmount);
            event.setCost(CNBConfig.hideCost);
            event.setMaterialCost(1);
            event.setOutput(output);
        }
    }
}
