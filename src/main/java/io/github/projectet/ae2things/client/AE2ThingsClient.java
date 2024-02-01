package io.github.projectet.ae2things.client;

import io.github.projectet.ae2things.item.AETItems;
import io.github.projectet.ae2things.item.DISKDrive;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class AE2ThingsClient {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(AE2ThingsClient::registerItemColors);
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(DISKDrive::getColor, AETItems.DISK_DRIVE_1K.get(),
                AETItems.DISK_DRIVE_4K.get(), AETItems.DISK_DRIVE_16K.get(), AETItems.DISK_DRIVE_64K.get(),
                AETItems.DISK_DRIVE_256K.get());
    }
}
