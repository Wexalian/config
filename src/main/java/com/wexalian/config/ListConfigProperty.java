package com.wexalian.config;

import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;
import com.wexalian.nullability.function.NonnullSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ListConfigProperty<T> extends BaseConfigProperty<List<T>> {
    private final List<Listener<T>> listeners = new ArrayList<>(0);
    private final NonnullSupplier<List<T>> defaultSupplier;
    private final Supplier<Collection<T>> defaultValuesSupplier;
    private List<T> values;
    
    //internal: see ConfigHandler
    ListConfigProperty(@Nonnull NonnullSupplier<List<T>> defaultSupplier, @Nullable NonnullSupplier<Collection<T>> defaultValuesSupplier) {
        this.defaultSupplier = defaultSupplier;
        this.defaultValuesSupplier = defaultValuesSupplier;
    }
    
    @Nonnull
    @Override
    public List<T> get() {
        return getOrSetDefault();
    }
    
    public void set(@Nonnull List<T> value) {
        List<T> old = this.values;
        this.values = value;
        this.listeners.forEach(l -> l.onChange(old, values));
        this.dirty = true;
    }
    
    public void set(int index, @Nonnull T value) {
        T old = this.getOrSetDefault().set(index, value);
        this.listeners.forEach(l -> l.onChange(List.of(old), List.of(value)));
        this.dirty = true;
    }
    
    public void add(@Nonnull T value) {
        this.getOrSetDefault().add(value);
        this.listeners.forEach(l -> l.onChange(List.of(), List.of(value)));
        this.dirty = true;
    }
    
    public void add(int index, @Nonnull T value) {
        this.getOrSetDefault().add(index, value);
        this.listeners.forEach(l -> l.onChange(List.of(), List.of(value)));
        this.dirty = true;
    }
    
    public void clear() {
        List<T> old = List.copyOf(this.getOrSetDefault());
        this.getOrSetDefault().clear();
        this.listeners.forEach(l -> l.onChange(old, List.of()));
        this.dirty = true;
    }
    
    public boolean remove(@Nonnull T value) {
        boolean success = this.getOrSetDefault().remove(value);
        if(success) {
            this.listeners.forEach(l -> l.onChange(List.of(value), List.of()));
            this.dirty = true;
        }
        return success;
    }
    
    @Nullable
    public T remove(int index) {
        T value = this.getOrSetDefault().remove(index);
        if(value != null) {
            this.listeners.forEach(l -> l.onChange(List.of(value), List.of()));
            this.dirty = true;
        }
        return value;
    }
    
    public void addListener(@Nonnull Listener<T> listener) {
        listeners.add(listener);
    }
    
    @Nonnull
    private List<T> getOrSetDefault() {
        if (values == null) {
            this.values = defaultSupplier.get();
            if (defaultValuesSupplier != null) {
                Collection<T> defaultValues = defaultValuesSupplier.get();
                this.values.addAll(defaultValues);
            }
            listeners.forEach(l -> l.onChange(List.of(), values));
            this.dirty = true;
        }
        return values;
    }
    
    //internal
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.values = (List<T>) value;
    }
    
    @FunctionalInterface
    public interface Listener<T> {
        void onChange(@Nonnull List<T> removed, @Nonnull List<T> added);
    }
}
