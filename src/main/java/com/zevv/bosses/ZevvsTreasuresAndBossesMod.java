package com.zevv.bosses;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ZevvsTreasuresAndBossesMod implements ModInitializer {
    public static final String MOD_ID = "zevvs_treasures_bosses";

    public static final Block ENDERITE_ORE = registerBlock("enderite_ore",
            new Block(AbstractBlock.Settings.copy(Blocks.ANCIENT_DEBRIS).hardness(30.0f).resistance(1200.0f)));
    public static final Block DARKENED_OBSIDIAN = registerBlock("darkened_obsidian",
            new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).hardness(60.0f).resistance(1200.0f)));

    public static final Item ENDERITE = registerItem("enderite", new Item(new Item.Settings()));
    public static final Item ENDERITE_SLATE = registerItem("enderite_slate", new Item(new Item.Settings()));
    public static final Item OBSIDIAN_GEM = registerItem("obsidian_gem", new Item(new Item.Settings()));
    public static final Item OBSIDIAN_SHARD = registerItem("obsidian_shard", new Item(new Item.Settings()));
    public static final Item TINY_OBSIDIAN_GEM = registerItem("tiny_obsidian_gem", new Item(new Item.Settings()));
    public static final Item OBSIDIAN_TIPPED_ARROW = registerItem("obsidian_tipped_arrow", new Item(new Item.Settings()));

    public static final EntityType<AbyssalCreatureEntity> ABYSSAL_CREATURE = Registry.register(
            Registries.ENTITY_TYPE, id("abyssal_creature"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AbyssalCreatureEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 2.1f)).build()
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(ABYSSAL_CREATURE, AbyssalCreatureEntity.createBossAttributes());
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, id(name), item);
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
        Registry.register(Registries.ITEM, id(name), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static class AbyssalCreatureEntity extends HostileEntity {
        public AbyssalCreatureEntity(EntityType<? extends HostileEntity> type, World world) {
            super(type, world);
        }

        public static DefaultAttributeContainer.Builder createBossAttributes() {
            return MobEntity.createMobAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 2000.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.40D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 50.0D);
        }

        @Override
        public boolean damage(DamageSource source, float amount) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof ServerPlayerEntity player) {
                if (player.isCreative()) {
                    player.sendMessage(Text.literal("You can't cheat to beat me."), false);
                    player.damage(this.getDamageSources().genericKill(), Float.MAX_VALUE);
                    return false;
                }
            }
            return super.damage(source, amount);
        }
    }
}
