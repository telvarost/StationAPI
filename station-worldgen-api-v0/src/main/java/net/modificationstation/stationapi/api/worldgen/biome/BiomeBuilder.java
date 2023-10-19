package net.modificationstation.stationapi.api.worldgen.biome;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.structure.Structure;
import net.modificationstation.stationapi.api.worldgen.structure.DefaultStructures;
import net.modificationstation.stationapi.api.worldgen.surface.SurfaceRule;
import net.modificationstation.stationapi.impl.worldgen.BiomeColorsImpl;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class BiomeBuilder {
    private static final ThreadLocal<BiomeBuilder> INSTANCES = ThreadLocal.withInitial(BiomeBuilder::new);
    private final Reference2IntMap<Class<? extends Entity>> hostileEntities = new Reference2IntOpenHashMap<>(32);
    private final Reference2IntMap<Class<? extends Entity>> passiveEntities = new Reference2IntOpenHashMap<>(32);
    private final Reference2IntMap<Class<? extends Entity>> waterEntities = new Reference2IntOpenHashMap<>(32);
    private final List<Structure> structures = new ArrayList<>();
    private final List<SurfaceRule> rules = new ArrayList<>();
    private BiomeColorProvider grassColor;
    private BiomeColorProvider leavesColor;
    private BiomeColorProvider fogColor;
    private boolean noDimensionStructures;
    private boolean precipitation;
    private boolean snow;
    private String name;
    private int minHeight;
    private int maxHeight;

    private BiomeBuilder() {}

    /**
     * Start biome building process with specified biome name
     */
    public static BiomeBuilder start(String name) {
        BiomeBuilder instance = INSTANCES.get();

        instance.name = name;
        instance.noDimensionStructures = false;
        instance.precipitation = true;
        instance.snow = false;
        instance.minHeight = 40;
        instance.maxHeight = 128;

        instance.grassColor = BiomeColorsImpl.DEFAULT_GRASS_COLOR;
        instance.leavesColor = BiomeColorsImpl.DEFAULT_LEAVES_COLOR;
        instance.fogColor = BiomeColorsImpl.DEFAULT_FOG_COLOR;

        instance.hostileEntities.clear();
        instance.passiveEntities.clear();
        instance.waterEntities.clear();
        instance.structures.clear();
        instance.rules.clear();

        return instance;
    }

    /**
     * Add surface rule to the biome. Rules are added in the same order as this method is called.
     * Rules are applied in the same order, if rule is applied others in the chain will be not applied.
     */
    public BiomeBuilder surfaceRule(SurfaceRule rule) {
        rules.add(rule);
        return this;
    }
    
    /**
     * Disable default dimension structures for the biome (lakes, ores, etc)
     */
    public BiomeBuilder noDimensionStructures() {
        noDimensionStructures = true;
        return this;
    }
    
    /**
     * Add structure into the biome.
     * Biomes with empty structure list will generate same features as dimension decorator have
     */
    public BiomeBuilder structure(Structure structure) {
        structures.add(structure);
        return this;
    }
    
    /**
     * Add overworld lakes into the biome
     */
    public BiomeBuilder overworldLakes() {
        structure(DefaultStructures.WATER_LAKE_SCATTERED);
        structure(DefaultStructures.LAVA_LAKE_SCATTERED);
        return this;
    }
    
    /**
     * Add overworld ores into the biome
     */
    public BiomeBuilder overworldOres() {
        structure(DefaultStructures.DIRT_ORE_SCATTERED);
        structure(DefaultStructures.GRAVEL_ORE_SCATTERED);
        structure(DefaultStructures.COAL_ORE_SCATTERED);
        structure(DefaultStructures.IRON_ORE_SCATTERED);
        structure(DefaultStructures.GOLD_ORE_SCATTERED);
        structure(DefaultStructures.REDSTONE_ORE_SCATTERED);
        structure(DefaultStructures.DIAMOND_ORE_SCATTERED);
        structure(DefaultStructures.LAPIS_LAZULI_ORE_SCATTERED);
        return this;
    }

    /**
     * Set if biome have rain or snow
     */
    public BiomeBuilder precipitation(boolean precipitation) {
        this.precipitation = precipitation;
        return this;
    }

    /**
     * Set that biome will have snow instead of rain
     */
    public BiomeBuilder snow(boolean snow) {
        this.snow = snow;
        return this;
    }

    /**
     * Add hostile entity (mobs/monsters) to spawn list.
     * Larger rarity value = more frequent entity spawn compared to other entities
     */
    public BiomeBuilder hostileEntity(Class<? extends Entity> entity, int rarity) {
        hostileEntities.put(entity, rarity);
        return this;
    }

    /**
     * Add passive entity (animals) to spawn list.
     * Larger rarity value = more frequent entity spawn compared to other entities
     */
    public BiomeBuilder passiveEntity(Class<? extends Entity> entity, int rarity) {
        passiveEntities.put(entity, rarity);
        return this;
    }

    /**
     * Add water entity (water animals) to spawn list.
     * Larger rarity value = more frequent entity spawn compared to other entities
     */
    public BiomeBuilder waterEntity(Class<? extends Entity> entity, int rarity) {
        waterEntities.put(entity, rarity);
        return this;
    }

    /**
     * Set biome grass color
     */
    public BiomeBuilder grassColor(BiomeColorProvider provider) {
        grassColor = provider;
        return this;
    }

    /**
     * Set biome grass color
     */
    public BiomeBuilder grassColor(int color) {
        return grassColor((source, x, z) -> color);
    }

    /**
     * Set biome leaves (foliage) color, change only oak-like leaves color
     */
    public BiomeBuilder leavesColor(BiomeColorProvider provider) {
        leavesColor = provider;
        return this;
    }

    /**
     * Set biome leaves (foliage) color, change only oak-like leaves color
     */
    public BiomeBuilder leavesColor(int color) {
        return leavesColor((source, x, z) -> color);
    }

    /**
     * Set biome grass and leaves (foliage) color.
     * Leaves color change only oak-like leaves color
     */
    public BiomeBuilder grassAndLeavesColor(BiomeColorProvider provider) {
        grassColor(provider);
        leavesColor(provider);
        return this;
    }

    /**
     * Set biome grass and leaves (foliage) color.
     * Leaves color change only oak-like leaves color
     */
    public BiomeBuilder grassAndLeavesColor(int color) {
        return grassAndLeavesColor((source, x, z) -> color);
    }

    /**
     * Set biome fog color
     */
    public BiomeBuilder fogColor(BiomeColorProvider provider) {
        fogColor = provider;
        return this;
    }

    /**
     * Set biome fog color
     */
    public BiomeBuilder fogColor(int color) {
        return fogColor((source, x, z) -> color);
    }

    /**
     * Set height range for the biome
     */
    public BiomeBuilder height(int minHeight, int maxHeight) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        return this;
    }

    public Biome build() {
        Biome biome = new TemplateBiome(name);

        rules.forEach(biome::addSurfaceRule);
        biome.setPrecipitation(precipitation);
        biome.setSnow(snow);

        biome.setGrassColor(grassColor);
        biome.setLeavesColor(leavesColor);
        biome.setFogColor(fogColor);
        biome.setMinHeight(minHeight);
        biome.setMaxHeight(maxHeight);
        biome.setNoDimensionStrucutres(noDimensionStructures);

        hostileEntities.forEach(biome::addHostileEntity);
        passiveEntities.forEach(biome::addPassiveEntity);
        waterEntities.forEach(biome::addWaterEntity);
        biome.getStructures().addAll(structures);

        return biome;
    }
}