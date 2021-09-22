package net.modificationstation.stationapi.impl.client.texture;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TextureBinder;
import net.minecraft.client.texture.TextureManager;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.client.event.texture.TexturePackLoadedEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.registry.ModelRegistry;
import net.modificationstation.stationapi.api.client.texture.TexturePackDependent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.client.texture.atlas.JsonModelAtlas;
import net.modificationstation.stationapi.api.client.texture.atlas.SquareAtlas;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.mixin.render.client.TessellatorAccessor;
import net.modificationstation.stationapi.mixin.render.client.TextureManagerAccessor;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.*;

import static net.modificationstation.stationapi.api.registry.Identifier.of;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class StationRenderAPI {

    @Entrypoint.ModID
    public static final ModID MODID = Null.get();

    public static SquareAtlas
            TERRAIN,
            GUI_ITEMS;

    public static ExpandableAtlas
            STATION_TERRAIN,
            STATION_GUI_ITEMS;

    public static JsonModelAtlas STATION_JSON_MODELS;

    @EventListener(priority = ListenerPriority.HIGH)
    private static void init(TextureRegisterEvent event) {
        TERRAIN = new SquareAtlas("/terrain.png", 16).setTessellator(Tessellator.INSTANCE);
        GUI_ITEMS = new SquareAtlas("/gui/items.png", 16);
        STATION_TERRAIN = new ExpandableAtlas(of(StationAPI.MODID, "terrain"), TERRAIN).initTessellator();
        STATION_GUI_ITEMS = new ExpandableAtlas(of(StationAPI.MODID, "gui_items"), GUI_ITEMS);
        STATION_JSON_MODELS = new JsonModelAtlas(of(StationAPI.MODID, "json_textures")).setTessellator(TessellatorAccessor.newInst(8388608));
        //noinspection deprecation
        TextureManager textureManager = ((Minecraft) FabricLoader.getInstance().getGameInstance()).textureManager;
        TextureBinder textureBinder;
        textureManager.addTextureBinder(new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FLOWING_WATER.texture), new StationStillWaterTextureBinder(), "/custom_water_still.png"));
        textureBinder = new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FLOWING_WATER.texture + 1), new StationFlowingWaterTextureBinder(), "/custom_water_flowing.png");
        textureBinder.textureSize = 2;
        textureManager.addTextureBinder(textureBinder);
        textureManager.addTextureBinder(new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FLOWING_LAVA.texture), new StationStillLavaTextureBinder(), "/custom_lava_still.png"));
        textureBinder = new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FLOWING_LAVA.texture + 1), new StationFlowingLavaTextureBinder(), "/custom_lava_flowing.png");
        textureBinder.textureSize = 2;
        textureManager.addTextureBinder(textureBinder);
        textureManager.addTextureBinder(new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FIRE.texture), new StationFireTextureBinder(0), "/custom_fire_e_w.png"));
        textureManager.addTextureBinder(new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.FIRE.texture + 16), new StationFireTextureBinder(1), "/custom_fire_n_s.png"));
        textureManager.addTextureBinder(new StationVanillaTextureBinder(TERRAIN.getTexture(BlockBase.PORTAL.texture), new StationPortalTextureBinder(), "/custom_portal.png"));
        textureManager.addTextureBinder(new StationCompassTextureBinder());
        textureManager.addTextureBinder(new StationClockTextureBinder());
    }

    @EventListener(priority = ListenerPriority.HIGH)
    private static void beforeTexturePackApplied(TexturePackLoadedEvent.Before event) {
        Map<String, Integer> textureMap = ((TextureManagerAccessor) event.textureManager).getTextures();
        textureMap.keySet().stream().filter(s -> event.newTexturePack.getResourceAsStream(s) == null).collect(Collectors.toList()).forEach(s -> GL11.glDeleteTextures(textureMap.remove(s)));
    }

    @EventListener(priority = ListenerPriority.HIGH)
    private static void texturePackApplied(TexturePackLoadedEvent.After event) {
        Atlas.getAtlases().forEach(atlas -> atlas.reloadFromTexturePack(event.newTexturePack));
        ModelRegistry.INSTANCE.forEach((identifier, model) -> model.reloadFromTexturePack(event.newTexturePack));
        new ArrayList<>(((TextureManagerAccessor) event.textureManager).getTextureBinders()).stream().filter(textureBinder -> textureBinder instanceof TexturePackDependent).forEach(textureBinder -> ((TexturePackDependent) textureBinder).reloadFromTexturePack(event.newTexturePack));
    }
}