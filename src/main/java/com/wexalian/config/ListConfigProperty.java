package com.wexalian.config;

import com.wexalian.common.collection.wrapper.ListWrapper;
import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;
import com.wexalian.nullability.function.NonnullSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ListConfigProperty<T> extends BaseConfigProperty<List<T>> implements ListWrapper<T> {
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
    
    @Override
    @Nullable
    public T set(int index, @Nonnull T value) {
        T old = this.getOrSetDefault().set(index, value);
        this.listeners.forEach(l -> l.onChange(List.of(old), List.of(value)));
        this.dirty = true;
        return old;
    }
    
    @Override
    public boolean add(@Nonnull T value) {
        this.getOrSetDefault().add(value);
        this.listeners.forEach(l -> l.onChange(List.of(), List.of(value)));
        this.dirty = true;
        return true;
    }
    
    @Override
    public void add(int index, @Nonnull T value) {
        this.getOrSetDefault().add(index, value);
        this.listeners.forEach(l -> l.onChange(List.of(), List.of(value)));
        this.dirty = true;
    }
    
    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        boolean success = this.getOrSetDefault().addAll(collection);
        if(success){
            this.listeners.forEach(l -> l.onChange(List.of(), List.copyOf(collection)));
            this.dirty = true;
        }
        return success;
    }
    
    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends T> collection) {
        boolean success = this.getOrSetDefault().addAll(index, collection);
        if(success){
            this.listeners.forEach(l -> l.onChange(List.of(), List.copyOf(collection)));
            this.dirty = true;
        }
        return success;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(@Nonnull Object value) {
        boolean success = this.getOrSetDefault().remove(value);
        if(success) {
            this.listeners.forEach(l -> l.onChange((List<T>)List.of(value), List.of()));
            this.dirty = true;
        }
        return success;
    }
    
    @Nullable
    @Override
    public T remove(int index) {
        T value = this.getOrSetDefault().remove(index);
        if(value != null) {
            this.listeners.forEach(l -> l.onChange(List.of(value), List.of()));
            this.dirty = true;
        }
        return value;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(@Nonnull Collection<?> collection) {
        boolean success = this.getOrSetDefault().removeAll(collection);
        if(success){
            this.listeners.forEach(l -> l.onChange(List.copyOf((Collection<T>)collection), List.of()));
            this.dirty = true;
        }
        return success;
    }
    
    @Override
    public void clear() {
        List<T> old = List.copyOf(this.getOrSetDefault());
        this.getOrSetDefault().clear();
        this.listeners.forEach(l -> l.onChange(old, List.of()));
        this.dirty = true;
    }
    
    //internal
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
    
    @Override
    @SuppressWarnings("unchecked")
    void setRaw(Object value) {
        this.values = (List<T>) value;
    }
    
    //listener
    public void addListener(@Nonnull Listener<T> listener) {
        listeners.add(listener);
    }
    
    @FunctionalInterface
    public interface Listener<T> {
        void onChange(@Nonnull List<T> removed, @Nonnull List<T> added);
    }
}
