package com.wexalian.config;

import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;
import com.wexalian.nullability.function.NonnullSupplier;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ListConfigProperty<T> extends BaseConfigProperty<List<T>> {
    private final NonnullSupplier<List<T>> defaultSupplier;
    private final Supplier<Collection<T>> defaultValuesSupplier;
    private List<T> values;
    
    ListConfigProperty(@Nonnull NonnullSupplier<List<T>> defaultSupplier, @Nullable Supplier<Collection<T>> defaultValuesSupplier) {
        this.defaultSupplier = defaultSupplier;
        this.defaultValuesSupplier = defaultValuesSupplier;
    }
    
    @Nonnull
    @Override
    public List<T> get() {
        return getOrSetDefault();
    }
    
    public void set(@Nonnull List<T> value) {
        this.values = value;
        this.dirty = true;
    }
    
    public void set(int index, @Nonnull T value) {
        this.getOrSetDefault().set(index, value);
        this.dirty = true;
    }
    
    public void add(@Nonnull T value) {
        this.getOrSetDefault().add(value);
        this.dirty = true;
    }
    
    public void add(int index, @Nonnull T value) {
        this.getOrSetDefault().add(index, value);
        this.dirty = true;
    }
    
    public void clear() {
        this.getOrSetDefault().clear();
        this.dirty = true;
    }
    
    public boolean remove(@Nonnull T object) {
        boolean success = this.getOrSetDefault().remove(object);
        this.dirty |= success;
        return success;
    }
    
    @Nullable
    public T remove(int index) {
        T value = this.getOrSetDefault().remove(index);
        this.dirty |= (value != null);
        return value;
    }
    
    @Nonnull
    private List<T> getOrSetDefault() {
        if (values == null) {
            set(defaultSupplier.get());
            if (values != null && defaultValuesSupplier != null) {
                values.addAll(defaultValuesSupplier.get());
            }
        }
        return values;
    }
    
    //internal
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.values = (List<T>) value;
    }
}
