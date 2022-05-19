module com.wexalian.config {
    requires transitive com.google.gson;
    
    requires com.wexalian.nullability;
    requires java.logging;
    
    exports com.wexalian.config;
    
    opens com.wexalian.config to com.google.gson;
}