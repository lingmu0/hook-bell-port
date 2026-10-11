package net.xuwu.hookbellport.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Fabric configuration; loaded once at startup on either physical side. */
public final class HookBellConfig {
    private static Set<String> blacklist = Set.of();

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("hookbellport-common.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, gson.toJson(new Config()) + "\n", StandardCharsets.UTF_8);
            }
            Config config = gson.fromJson(Files.readString(path, StandardCharsets.UTF_8), Config.class);
            if (config == null || config.hook_bell == null || config.hook_bell.blacklist == null) {
                throw new IllegalArgumentException("Expected hook_bell.blacklist array");
            }
            blacklist = Set.copyOf(new HashSet<>(config.hook_bell.blacklist));
        } catch (IOException | RuntimeException exception) {
            LoggerFactory.getLogger("hookbellport").error("Could not load {}; using an empty blacklist", path, exception);
        }
    }

    public static boolean isBlacklisted(EntityType<?> entityType) {
        return blacklist.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }

    private static final class Config { private Bell hook_bell = new Bell(); }
    private static final class Bell { private List<String> blacklist = new ArrayList<>(); }
    private HookBellConfig() {}
}
