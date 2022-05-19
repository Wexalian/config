package com.wexalian.config;

import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;
import com.wexalian.nullability.function.NonnullSupplier;

import java.util.List;

public class ListConfigProperty<T> extends BaseConfigProperty<List<T>> {
    private final NonnullSupplier<List<T>> defaultSupplier;
    private List<T> values;
    
    ListConfigProperty(@Nonnull NonnullSupplier<List<T>> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }
    
    @Nonnull
    @Override
    public List<T> get() {
        if (values == null) {
            set(defaultSupplier.get());
        }
        if (values == null) {
            return List.of();
        }
        return List.copyOf(values);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.values = (List<T>) value;
    }
    
    public void set(List<T> value) {
        this.values = value;
        this.dirty = true;
    }
    
    public void set(int index, T value) {
        this.getOrDefault().set(index, value);
        this.dirty = true;
    }
    
    @Nonnull
    private List<T> getOrDefault() {
        if (values == null) {
            set(defaultSupplier.get());
        }
        return values;
    }
    
    public void add(T value) {
        this.getOrDefault().add(value);
        this.dirty = true;
    }
    
    public void add(int index, T value) {
        this.getOrDefault().add(index, value);
        this.dirty = true;
    }
    
    public void clear() {
        this.getOrDefault().clear();
        this.dirty = true;
    }
    
    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        boolean success = this.getOrDefault().remove((T) object);
        this.dirty |= success;
        return success;
    }
    
    @Nullable
    public T remove(int index) {
        T value = this.getOrDefault().remove(index);
        this.dirty |= (value != null);
        return value;
    }
}
