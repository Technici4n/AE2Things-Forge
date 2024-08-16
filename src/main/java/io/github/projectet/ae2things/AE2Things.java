package io.github.projectet.ae2things;

import io.github.projectet.ae2things.client.AE2ThingsClient;
import io.github.projectet.ae2things.command.Command;
import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.util.StorageManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

@Mod(AE2Things.MOD_ID)
public class AE2Things {

    public static final String MOD_ID = "ae2things";

    public static StorageManager STORAGE_INSTANCE = new StorageManager();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public AE2Things(IEventBus modEventBus, Dist dist) {
        ITEMS.register(modEventBus);

        AETItems.init();

        modEventBus.addListener(AE2Things::commonSetup);
        modEventBus.addListener(AE2Things::addContentsToCreativeTab);

        NeoForge.EVENT_BUS.addListener(Command::commandRegister);
        NeoForge.EVENT_BUS.addListener(AE2Things::worldTick);

        if (dist.isClient()) {
            AE2ThingsClient.init(modEventBus);
        }
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

    public static void worldTick(LevelTickEvent.Pre event) {
        var level = event.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            STORAGE_INSTANCE = StorageManager.getInstance(serverLevel.getServer());
        }
    }
}
