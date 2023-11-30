package io.github.projectet.ae2things.compat;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberRootPanel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

import appeng.core.AppEng;

@JeiPlugin
public class REI implements IModPlugin {
    ResourceLocation ID = AppEng.makeId("inscriber");

    @Override
    public ResourceLocation getPluginUid() {
        return AE2Things.id("plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AE2Things.ADVANCED_INSCRIBER_ITEM.get()), ID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                AdvancedInscriberRootPanel.class,
                82, 39, 26, 16,
                ID);
    }
}
