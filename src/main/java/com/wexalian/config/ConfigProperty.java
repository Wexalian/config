package com.wexalian.config;

import com.wexalian.nullability.annotations.Nonnull;
import com.wexalian.nullability.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigProperty<T> extends BaseConfigProperty<T> {
    private final List<Listener<T>> listeners = new ArrayList<>(0);
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
            T old = this.value;
            this.value = value;
            listeners.forEach(l -> l.onChange(old, value));
            this.dirty = true;
        }
    }
    
    public void addListener(@Nonnull Listener<T> listener) {
        listeners.add(listener);
    }
    
    //internal
    @SuppressWarnings("unchecked")
    @Override
    void setRaw(Object value) {
        this.value = (T) value;
    }
    
    @FunctionalInterface
    public interface Listener<T> {
        void onChange(@Nullable T oldV, @Nullable T newV);
    }
}
