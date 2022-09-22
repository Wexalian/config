package com.wexalian.config;

import com.wexalian.nullability.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class ConfigProperty<T> extends BaseConfigProperty<T> {
    private final Supplier<T> defaultSupplier;
    private T value;
    
    //internal: see ConfigHandler
    ConfigProperty(@Nullable Supplier<T> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }
    
    @Nullable
    public T get() {
        if (value == null && defaultSupplier != null) {
            set(defaultSupplier.get());
        }
        return value;
    }
    
    public void set(@Nullable T value) {
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            this.dirty = true;
        }
    }
    
    //internal
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.value = (T) value;
    }
}
