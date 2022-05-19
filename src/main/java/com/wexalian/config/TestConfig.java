package com.wexalian.config;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public class TestConfig {
    private static final Path PATH = Path.of(System.getProperty("user.dir")).resolve("test-config.json");
    private static final Random RANDOM = new Random();
    
    public static void main(String[] args) throws IOException {
        testConfig();
    }
    
    public static void testConfig() throws IOException {
        ConfigHandler config = ConfigHandler.create("test-config");
        // config.setSerializeNulls();
        
        ConfigProperty<Integer> testInt3 = config.createIntegerProperty("category1#c", 0);
        ConfigProperty<Integer> testInt1 = config.createIntegerProperty("category1#a", 0);
        ConfigProperty<Integer> testInt2 = config.createIntegerProperty("category1#b", 0);
        ConfigProperty<Integer> testInt5 = config.createIntegerProperty("category2#e", 0);
        
        config.setCategory("category_test");
        
        ConfigProperty<Integer> testInt4 = config.createIntegerProperty("category2#d", 0);
        ConfigProperty<Integer> testInt6 = config.createIntegerProperty("category2#f", 0);
        
        ConfigProperty<String> testString = config.createProperty("strings#string1", null, new TypeToken<>() {});
        
        ConfigProperty<Double> testDouble = config.createDoubleProperty("double", 0);
        
        config.resetCategory();
        
        config.load(PATH);
        
        config.debug();
        
        testInt1.set(RANDOM.nextInt(10));
        testInt2.set(RANDOM.nextInt(10));
        testInt3.set(RANDOM.nextInt(10));
        testInt4.set(RANDOM.nextInt(10));
        testInt5.set(RANDOM.nextInt(10));
        testInt6.set(RANDOM.nextInt(10));
        
        testString.set("test" + RANDOM.nextInt(10));
        
        testDouble.set(RANDOM.nextDouble() * 10);
        
        config.save();
    }
}
