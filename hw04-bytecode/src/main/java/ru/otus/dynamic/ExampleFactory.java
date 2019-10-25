package ru.otus.dynamic;

import ru.otus.Example;
import ru.otus.ExampleInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ExampleFactory {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private static final ExampleInterface originalClassInstance = new Example();
    private static final InvocationHandler handler = new Invoker();

    private static ExampleInterface patchedClassInstance;

    static {
        patchedClassInstance = (ExampleInterface) Proxy.newProxyInstance(
                ExampleFactory.class.getClassLoader(),
                new Class<?>[]{ExampleInterface.class},
                handler
        );
    }

    public static ExampleInterface getExample() {
        return patchedClassInstance;
    }

    static class Invoker implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Arrays.stream(method.getDeclaredAnnotations())
                    .filter(a -> a.annotationType().getSimpleName().equals("MagicAspectLogging"))
                    .forEach(a -> printArgs(args));
            return method.invoke(originalClassInstance, args);
        }
    }

    private static void printArgs(Object[] args) {
        System.out.println(
                String.format("%sAspect Logger says: Method invoked for instance %s, arguments: %s %s",
                        ANSI_GREEN,
                        originalClassInstance,
                        Arrays.toString(args),
                        ANSI_RESET
                )
        );
    }
}
