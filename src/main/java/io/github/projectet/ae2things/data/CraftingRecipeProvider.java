package io.github.projectet.ae2things.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.AETItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

public class CraftingRecipeProvider extends RecipeProvider {
    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        driveRecipes(output);
        housingRecipe(output);
    }

    private void housingRecipe(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AETItems.DISK_HOUSING)
                .pattern("aba")
                .pattern("b b")
                .pattern("ded")
                .define('a', AEBlocks.QUARTZ_GLASS)
                .define('b', Tags.Items.DUSTS_REDSTONE)
                .define('d', Tags.Items.INGOTS_NETHERITE)
                .define('e', Tags.Items.GEMS_AMETHYST)
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .save(output, AE2Things.id("disk_housing"));
    }

    private static void driveRecipes(RecipeOutput output) {
        var componentsToDrive = Map.of(
                AEItems.CELL_COMPONENT_1K, AETItems.DISK_DRIVE_1K,
                AEItems.CELL_COMPONENT_4K, AETItems.DISK_DRIVE_4K,
                AEItems.CELL_COMPONENT_16K, AETItems.DISK_DRIVE_16K,
                AEItems.CELL_COMPONENT_64K, AETItems.DISK_DRIVE_64K,
                AEItems.CELL_COMPONENT_256K, AETItems.DISK_DRIVE_256K);

        for (var entry : componentsToDrive.entrySet()) {
            var component = entry.getKey();
            var drive = entry.getValue();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, drive.get())
                    .pattern("aba")
                    .pattern("bcb")
                    .pattern("ded")
                    .define('a', AEBlocks.QUARTZ_GLASS)
                    .define('b', Tags.Items.DUSTS_REDSTONE)
                    .define('c', component)
                    .define('d', Tags.Items.INGOTS_NETHERITE)
                    .define('e', Tags.Items.GEMS_AMETHYST)
                    .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                    .save(output, drive.getId());

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, drive.get())
                    .requires(AETItems.DISK_HOUSING)
                    .requires(component)
                    .unlockedBy("has_housing", has(AETItems.DISK_HOUSING))
                    .unlockedBy("has_component", has(component))
                    .save(output, drive.getId().withSuffix("_with_housing"));
        }
    }
}
