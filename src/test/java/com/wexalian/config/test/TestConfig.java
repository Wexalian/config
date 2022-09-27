package com.wexalian.config.test;

import com.google.gson.reflect.TypeToken;
import com.wexalian.config.ConfigHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TestConfig {
    private static final Path PATH = Path.of(System.getProperty("user.dir")).resolve("test-config.json");
    private static final Random RANDOM = new Random();
    
    public static void main(String[] args) throws IOException {
        testConfig();
    }
    
    public static void testConfig() throws IOException {
        ConfigHandler config = ConfigHandler.create("test-config");
        // config.setSerializeNulls();
        
        var testInt3 = config.createIntegerProperty("category1#int1", 0);
        var testInt1 = config.createIntegerProperty("category1#int2", 0);
        var testInt2 = config.createIntegerProperty("category1#int3", 0);
        var testInt5 = config.createIntegerProperty("category2#int4", 0);
        
        config.pushCategory("test1");
        
        var testInt4 = config.createIntegerProperty("category2#int5", 0);
        var testInt6 = config.createIntegerProperty("int6", 0);
        var testString1 = config.createProperty("string1", null, new TypeToken<>() {});
        
        config.popCategory();
        config.pushCategory("test2");
        
        var testList1 = config.createListProperty("level1#level2#list1", () -> Set.of("default1"), new TypeToken<>() {});
        var testString2 = config.createStringProperty("level1#level2#level3#string2", "");
    
        Map<String, Integer> testMap1 = config.createMapProperty("level1#map1", new TypeToken<>(){});
        
        config.popCategory();
    
        var testDouble1 = config.createDoubleProperty("double1", 0);
        
        
        config.load(PATH);
        
        config.debug();
        
        testInt1.set(RANDOM.nextInt(10));
        testInt2.set(RANDOM.nextInt(10));
        testInt3.set(RANDOM.nextInt(10));
        testInt4.set(RANDOM.nextInt(10));
        testInt5.set(RANDOM.nextInt(10));
        testInt6.set(RANDOM.nextInt(10));
        
        testString1.set("test" + RANDOM.nextInt(10));
        
        testString2.set("test" + RANDOM.nextInt(10));
        
        testList1.clear();
        int count = RANDOM.nextInt(10) + 1;
        for (int i = 0; i < count; i++) {
            testList1.add("value" + RANDOM.nextInt(10));
        }
        
        testDouble1.set(RANDOM.nextDouble() * 10);
    
        testMap1.clear();
        count = RANDOM.nextInt(10) + 1;
        for (int i = 0; i < count; i++) {
            testMap1.put("value" + RANDOM.nextInt(10), RANDOM.nextInt(10));
        }
        config.save();
    }
}
