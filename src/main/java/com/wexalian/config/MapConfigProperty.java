package com.wexalian.config;

import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;
import com.wexalian.nullability.function.NonnullSupplier;

import java.util.*;
import java.util.function.Supplier;

public class MapConfigProperty<K, V> extends BaseConfigProperty<Map<K, V>> implements Map<K, V> {
    private final List<Listener<K, V>> listeners = new ArrayList<>(0);
    private final NonnullSupplier<Map<K, V>> defaultSupplier;
    private final Supplier<Map<K, V>> defaultValuesSupplier;
    private Map<K, V> values;
    
    //internal: see ConfigHandler
    MapConfigProperty(@Nonnull NonnullSupplier<Map<K, V>> defaultSupplier, @Nullable NonnullSupplier<Map<K, V>> defaultValuesSupplier) {
        this.defaultSupplier = defaultSupplier;
        this.defaultValuesSupplier = defaultValuesSupplier;
    }
    
    @Override
    public Map<K, V> get() {
        return getOrSetDefault();
    }
    
    
    public void set(@Nonnull Map<K, V> value) {
        Map<K, V> old = this.values;
        this.values = value;
        this.listeners.forEach(l -> l.onChange(old, this.values));
        this.dirty = true;
    }
    
    @Override
    @Nullable
    public V put(@Nonnull K key, @Nonnull V value) {
        V old = this.getOrSetDefault().put(key, value);
        this.listeners.forEach(l -> l.onChange(old == null? Map.of() : Map.of(key, old), Map.of(key, value)));
        this.dirty = true;
        return old;
    }
    
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public V remove(@Nonnull Object key) {
        V value = this.getOrSetDefault().remove(key);
        this.listeners.forEach(l -> l.onChange(Map.of((K)key,value), Map.of()));
        this.dirty = true;
        return value;
    }
    
    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends V> map) {
        this.getOrSetDefault().putAll(map);
        this.listeners.forEach(l -> l.onChange(Map.of(), Map.copyOf(map)));
        this.dirty = true;
    }
    
    @Override
    public void clear() {
        Map<K,V> old = Map.copyOf(this.getOrSetDefault());
        this.getOrSetDefault().clear();
        this.listeners.forEach(l -> l.onChange(old, Map.of()));
        this.dirty = true;
    }
    
    //Map
    @Override
    public int size() {
        return this.getOrSetDefault().size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.getOrSetDefault().isEmpty();
    }
    
    @Override
    public boolean containsKey(@Nonnull Object key) {
        return this.getOrSetDefault().containsKey(key);
    }
    
    @Override
    public boolean containsValue(@Nonnull Object value) {
        return this.getOrSetDefault().containsValue(value);
    }
    
    @Override
    public V get(@Nonnull Object key) {
        return this.getOrSetDefault().get(key);
    }
    
    @Override
    @Nonnull
    public Set<K> keySet() {
        return this.getOrSetDefault().keySet();
    }
    
    @Override
    @Nonnull
    public Collection<V> values() {
        return this.getOrSetDefault().values();
    }
    
    @Override
    @Nonnull
    public Set<Entry<K, V>> entrySet() {
        return this.getOrSetDefault().entrySet();
    }
    
    //internal
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.values = (Map<K, V>) value;
    }
    
    @Nonnull
    private Map<K, V> getOrSetDefault() {
        if (values == null) {
            this.values = defaultSupplier.get();
            if (this.defaultValuesSupplier != null) {
                Map<K, V> defaultValues = this.defaultValuesSupplier.get();
                this.values.putAll(defaultValues);
            }
            this.listeners.forEach(l -> l.onChange(Map.of(), values));
            this.dirty = true;
        }
        return this.values;
    }
    
    //listener
    public void addListener(@Nonnull Listener<K,V> listener) {
        this.listeners.add(listener);
    }
    
    @FunctionalInterface
    public interface Listener<K, V> {
        void onChange(@Nonnull Map<K, V> removed, @Nonnull Map<K, V> added);
    }
}
