package io.github.projectet.ae2things.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import io.github.projectet.ae2things.AE2Things;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AE2Things.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AE2ThingsDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();

        var pack = generator.getVanillaPack(true);
        var existingFileHelper = event.getExistingFileHelper();

        // Recipes
        pack.addProvider(bindRegistries(CraftingRecipeProvider::new, registries));
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
            BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory,
            CompletableFuture<HolderLookup.Provider> factories) {
        return packOutput -> factory.apply(packOutput, factories);
    }
}
