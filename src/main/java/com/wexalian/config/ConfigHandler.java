package com.wexalian.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ConfigHandler {
    private static final Logger LOGGER = Logger.getLogger("com.wexalian.config");
    
    private final Map<String, BaseConfigProperty<?>> properties = new LinkedHashMap<>();
    private final Map<String, TypeToken<?>> typeTokens = new HashMap<>();
    
    private final String name;
    private Gson gson;
    
    private Path filePath;
    private boolean forceDirty;
    
    private ConfigHandler(String name, Gson gson) {
        this.name = name;
        this.gson = gson;
    }
    
    @Nonnull
    public <T> ConfigProperty<T> createProperty(@Nonnull String name, @Nonnull TypeToken<T> token) {
        return createProperty(name, null, token);
    }
    
    @Nonnull
    public <T> ConfigProperty<T> createProperty(@Nonnull String name, @Nullable Supplier<T> defaultSupplier, @Nonnull TypeToken<T> token) {
        ConfigProperty<T> property = new ConfigProperty<>(defaultSupplier);
        properties.put(name, property);
        typeTokens.put(name, token);
        return property;
    }
    
    @Nonnull
    public ConfigProperty<Byte> createByteProperty(@Nonnull String name, byte defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Short> createShortProperty(@Nonnull String name, short defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Integer> createIntegerProperty(@Nonnull String name, int defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Long> createLongProperty(@Nonnull String name, long defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Float> createFloatProperty(@Nonnull String name, float defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Double> createDoubleProperty(@Nonnull String name, double defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Boolean> createBooleanProperty(@Nonnull String name, boolean defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<Character> createCharacterProperty(@Nonnull String name, char defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public ConfigProperty<String> createStringProperty(@Nonnull String name, String defaultValue) {
        return createProperty(name, () -> defaultValue, new TypeToken<>() {});
    }
    
    @Nonnull
    public <T> ListConfigProperty<T> createListProperty(@Nonnull String name, @Nonnull TypeToken<List<T>> token) {
        ListConfigProperty<T> property = new ListConfigProperty<>(ArrayList::new);
        properties.put(name, property);
        typeTokens.put(name, token);
        return property;
    }
    
    public void load(@Nonnull Path path) throws IOException {
        this.filePath = path;
        
        if (Files.exists(path)) {
            String content = Files.readString(path);
            JsonElement json = JsonParser.parseString(content);
            if (json.isJsonObject()) {
                parseJsonObject(json.getAsJsonObject(), "");
            }
        }
        else forceDirty = true;
    }
    
    private void parseJsonObject(JsonObject json, String category) {
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();
            
            if (category != null && !category.isBlank()) {
                key = category + '#' + key;
            }
            
            BaseConfigProperty<?> property = properties.get(key);
            if (property != null) {
                TypeToken<?> token = typeTokens.get(key);
                Object value = gson.fromJson(element, token.getType());
                property.setRaw(value);
            }
            else {
                if (element.isJsonObject()) {
                    parseJsonObject(element.getAsJsonObject(), key);
                }
                else {
                    forceDirty = true;
                    LOGGER.warning("Problem loading config file '" + filePath.getFileName() + "', no property found for entry '" + entry.getKey() + "' with value '" + entry.getValue() + "'");
                }
            }
        }
    }
    
    public void save(@Nonnull Path path) throws IOException {
        this.filePath = path;
        save();
    }
    
    public void save() throws IOException {
        if (filePath != null) {
            if (forceDirty || properties.values().stream().anyMatch(BaseConfigProperty::isDirty)) {
                JsonObject json = new JsonObject();
                Map<String, JsonObject> categories = new HashMap<>();
                
                for (Entry<String, BaseConfigProperty<?>> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    BaseConfigProperty<?> property = entry.getValue();
                    
                    Object value = property.get();
                    TypeToken<?> token = typeTokens.get(key);
                    
                    if (token != null) {
                        JsonObject toAddTo = json;
                        
                        if (key.indexOf('#') > 0) {
                            String[] split = key.split("#");
                            key = split[1];
                            String category = split[0];
                            if (category != null && !category.isBlank()) {
                                toAddTo = categories.computeIfAbsent(category, s -> {
                                    JsonObject object = new JsonObject();
                                    json.add(category, object);
                                    return object;
                                });
                            }
                        }
                        JsonElement element = gson.toJsonTree(value, token.getType());
                        
                        toAddTo.add(key, element);
                    }
                }
                
                String content = gson.toJson(json);
                Files.writeString(filePath, content);
            }
        }
        else LOGGER.severe("No file path for config '" + name + "', please load from a file or provide a save path");
    }
    
    public void setSerializeNulls() {
        this.gson = gson.newBuilder().serializeNulls().create();
    }
    
    public static ConfigHandler create(String name) {
        return create(name, new GsonBuilder().setPrettyPrinting().create());
    }
    
    public static ConfigHandler create(String name, Gson gson) {
        return new ConfigHandler(name, gson);
    }
}
