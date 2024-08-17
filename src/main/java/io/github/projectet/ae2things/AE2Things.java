package io.github.projectet.ae2things;

import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.Nullable;

import io.github.projectet.ae2things.command.Command;
import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.util.StorageManager;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AECreativeTabIds;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

@Mod(AE2Things.MOD_ID)
public class AE2Things {

    public static final String MOD_ID = "ae2things";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(MOD_ID);

    public static final DataComponentType<UUID> DATA_DISK_ID = registerDataComponentType("disk_id", builder -> {
        builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC);
    });
    public static final DataComponentType<Long> DATA_DISK_ITEM_COUNT = registerDataComponentType("disk_item_count",
            builder -> {
                builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG);
            });
    public static final DataComponentType<FuzzyMode> DATA_FUZZY_MODE = registerDataComponentType("fuzzy_mode",
            builder -> {
                builder.persistent(FuzzyMode.CODEC).networkSynchronized(FuzzyMode.STREAM_CODEC);
            });

    @Nullable
    private static StorageManager storageManager;

    @Nullable
    private static MinecraftServer storageManagerServer;

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public AE2Things(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        COMPONENTS.register(modEventBus);

        AETItems.init();

        modEventBus.addListener(AE2Things::commonSetup);
        modEventBus.addListener(AE2Things::addContentsToCreativeTab);

        NeoForge.EVENT_BUS.addListener(Command::commandRegister);
        NeoForge.EVENT_BUS.addListener(AE2Things::onServerStarted);
        NeoForge.EVENT_BUS.addListener(AE2Things::onServerStopped);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        AETItems.commonSetup();

        StorageCells.addCellHandler(DISKCellHandler.INSTANCE);

        event.enqueueWork(() -> {
            var disksText = "text.ae2things.disk_drives";

            for (var cell : AETItems.DISK_DRIVES) {
                Upgrades.add(AEItems.FUZZY_CARD, cell.get(), 1, disksText);
                Upgrades.add(AEItems.INVERTER_CARD, cell.get(), 1, disksText);
            }
        });
    }

    public static void addContentsToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(AECreativeTabIds.MAIN)) {
            return;
        }

        event.accept(AETItems.DISK_HOUSING);

        for (var cell : AETItems.DISK_DRIVES) {
            event.accept(cell.get());
        }
    }

    private static void onServerStarted(ServerStartedEvent event) {
        storageManagerServer = event.getServer();
        storageManager = StorageManager.getInstance(event.getServer());
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        if (storageManagerServer == event.getServer()) {
            storageManagerServer = null;
            storageManager = null;
        }
    }

    private static <T> DataComponentType<T> registerDataComponentType(String name,
            Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        COMPONENTS.register(name, () -> componentType);
        return componentType;
    }

    @Nullable
    public static StorageManager currentStorageManager() {
        return storageManager;
    }
}
