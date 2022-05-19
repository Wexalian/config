package com.wexalian.config;

import com.wexalian.nullability.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class ConfigProperty<T> extends BaseConfigProperty<T> {
    private final Supplier<T> defaultSupplier;
    private T value;
    
    ConfigProperty(@Nullable Supplier<T> defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }
    
    public T get() {
        if (value == null && defaultSupplier != null) {
            set(defaultSupplier.get());
        }
        return value;
    }
    
    @SuppressWarnings("unchecked")
    void setRaw(Object value) {
        this.value = (T) value;
    }
    
    public void set(T value) {
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            this.dirty = true;
        }
    }
}
