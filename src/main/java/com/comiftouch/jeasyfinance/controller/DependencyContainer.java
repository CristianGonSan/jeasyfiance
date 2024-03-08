package com.comiftouch.jeasyfinance.controller;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {
    private static final DependencyContainer instance = new DependencyContainer();
    private final Map<Class<?>, Object> dependencyMap = new HashMap<>();

    private DependencyContainer() {
        // Inicializar dependencias predeterminadas si es necesario
    }

    public static DependencyContainer getInstance() {
        return instance;
    }

    public <T> void registerDependency(T instance) {
        dependencyMap.put(instance.getClass(), instance);
    }

    public <T> T getDependency(Class<T> type) {
        return type.cast(dependencyMap.get(type));
    }

    public void clear() {
        dependencyMap.clear();
    }
}
