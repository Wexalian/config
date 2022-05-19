package com.wexalian.config;

public abstract class BaseConfigProperty<T>{
    protected boolean dirty;
    
    abstract T get();
    
    abstract void setRaw(Object value);
    
    final boolean isDirty() {
        return dirty;
    }
}
