package ru.otus.dynamic;

import ru.otus.annotation.MagicAspectLogging;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MagicLoggingInjectedFactory {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final Set<String> PATCHED_METHODS = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static <T, U> U getInstance(
            Class<? extends T> className,
            Class<? extends U> desiredInterface,
            Object... constructorParams) {

        validateInterfaces(className.getInterfaces(), desiredInterface);

        Set<String> methodsWithLogging = Arrays.stream(className.getMethods())
                .filter(MagicLoggingInjectedFactory::isMagicLogging)
                .map(MagicLoggingInjectedFactory::extractNameAndParams)
                .collect(Collectors.toSet());

        PATCHED_METHODS.addAll(methodsWithLogging);

        try {
            return (U) Proxy.newProxyInstance(
                    MagicLoggingInjectedFactory.class.getClassLoader(),
                    new Class<?>[]{desiredInterface},
                    new Invoker(instantiate(className, constructorParams))
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while instantiating object, cause", e);
        }

    }

    private static String extractNameAndParams(Method method) {
        return method.getName() + Arrays.toString(method.getParameters());
    }

    private static boolean isMagicLogging(Method method) {
        return Arrays.stream(method.getAnnotations())
                .anyMatch(a -> a.annotationType().equals(MagicAspectLogging.class));
    }

    private static <U> void validateInterfaces(Class<?>[] interfaces, Class<? extends U> desiredInterface) {
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("The class should implement at least one interface");
        }
        Arrays.stream(interfaces).filter(x -> x.equals(desiredInterface)).findAny().orElseThrow(() -> {
            throw new IllegalArgumentException("The class should implement desired interface");
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiate(
            Class<? extends T> className,
            Object[] constructorParams
    ) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<?>[] constructors = className.getConstructors();
        Constructor<?> actualConstructor = Arrays.stream(constructors)
                .filter(constructor -> validateConstructor(
                        constructorParams,
                        constructor.getParameterCount(),
                        constructor.getParameterTypes()
                ))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(
                            String.format("Arguments passed to factory do not match constructors of %s", className)
                    );
                });
        return (T) actualConstructor.newInstance(constructorParams);
    }

    private static boolean validateConstructor(Object[] constructorParams, int parameterCount, Class<?>[] parameterTypes) {
        if (parameterCount != parameterTypes.length) {
            return false;
        } else {
            for (int i = 0; i < parameterCount; i++) {
                if (parameterTypes[i] != constructorParams[i].getClass()) {
                    return false;
                }
            }
        }
        return true;
    }

    static class Invoker<T> implements InvocationHandler {

        private final T instance;

        Invoker(T instance) {
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (PATCHED_METHODS.contains(extractNameAndParams(method))) {
                printArgs(args);
            }
            return method.invoke(instance, args);
        }
    }

    private static void printArgs(Object[] args) {
        System.out.println(
                String.format("%sAspect Logger says: Method invoked, arguments: %s %s",
                        ANSI_GREEN,
                        Arrays.toString(args),
                        ANSI_RESET
                )
        );
    }
}
