package io.github.projectet.ae2things.client;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.item.DISKDrive;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@Mod(value = AE2Things.MOD_ID, dist = Dist.CLIENT)
public class AE2ThingsClient {
    public AE2ThingsClient(IEventBus eventBus) {
        eventBus.addListener(AE2ThingsClient::registerItemColors);
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(DISKDrive::getColor, AETItems.DISK_DRIVE_1K.get(),
                AETItems.DISK_DRIVE_4K.get(), AETItems.DISK_DRIVE_16K.get(), AETItems.DISK_DRIVE_64K.get(),
                AETItems.DISK_DRIVE_256K.get());
    }
}
