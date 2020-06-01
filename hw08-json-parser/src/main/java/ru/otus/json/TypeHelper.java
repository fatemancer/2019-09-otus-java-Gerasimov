package ru.otus.json;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class TypeHelper {

    private static final Map<Class<?>, Function<Object, String>> TERMINAL_CONVERSION_MAP = Map.of(
            NullClass.class, (o) -> JsonValue.NULL.toString(),
            JsonValue.class, (o) -> ((JsonValue) o).toString(),
            JsonArrayBuilder.class, (o) -> ((JsonArrayBuilder) o).build().toString(),
            JsonObjectBuilder.class, (o) -> ((JsonObjectBuilder) o).build().toString()
    );

    private static final Map<Class<?>, Function<Object, JsonValue>> TERMINAL_VALUES_HANDLER_MAP = Map.ofEntries(
            Map.entry(NullClass.class, (o) -> JsonValue.NULL),
            Map.entry(Long.class, (o) -> Json.createValue((long) o)),
            Map.entry(Integer.class, (o) -> Json.createValue((int) o)),
            Map.entry(BigDecimal.class, (o) -> Json.createValue((BigDecimal) o)),
            Map.entry(BigInteger.class, (o) -> Json.createValue((BigInteger) o)),
            Map.entry(Boolean.class, (o) -> (boolean) o ? JsonValue.TRUE : JsonValue.FALSE),
            Map.entry(Double.class, (o) -> Json.createValue((double) o)),
            Map.entry(String.class, (o) -> Json.createValue((String) o)),
            Map.entry(Character.class, (o) -> Json.createValue(((Character) o).toString()) ),
            Map.entry(Float.class, (o) -> Json.createValue(((Float) o).doubleValue())),
            Map.entry(Byte.class, (o) -> Json.createValue(((Byte) o).intValue())),
            Map.entry(Short.class, (o) -> Json.createValue(((Short) o).intValue())),
            Map.entry(Collapsable.class, TypeHelper::collapseArray)
    );

    private static Set<Class<?>> TERMINAL_CLASSES = Set.of(
            BigDecimal.class,
            BigInteger.class,
            Boolean.class,
            Double.class,
            Integer.class,
            Long.class,
            String.class,
            Byte.class,
            Short.class,
            Float.class,
            Character.class
    );

    static Function<Object, String> getOutputValueHandler(
            Object rootObject,
            Function<Object, String> missingStrategyHandler
    ) {
        return TypeHelper.TERMINAL_CONVERSION_MAP.getOrDefault(
                TypeHelper.getTerminalClass(rootObject),
                missingStrategyHandler
        );
    }

    static Function<Object, JsonValue> getValueHandler(
            Class<?> object,
            Function<Object, JsonValue> missingStrategyHandler
    ) {
        return TypeHelper.TERMINAL_VALUES_HANDLER_MAP.getOrDefault(
                object,
                missingStrategyHandler
        );
    }

    static Class<?> getSupportedClass(Object v) {
        if (v == null) {
            return NullClass.class;
        }
        Class<?> aClass = v.getClass();
        if (Collection.class.isAssignableFrom(aClass)) {
            return Collection.class;
        } else if (isByteOrCharArray(v)) {
            return Collapsable.class;
        } else {
            return aClass;
        }
    }

    static boolean isCollection(Object o) {
        return Collection.class.isAssignableFrom(o.getClass());
    }

    static boolean isArray(Object o) {
        return o.getClass().isArray();
    }

    static boolean isByteOrCharArray(Object parsedValue) {
        return parsedValue instanceof char[] || parsedValue instanceof byte[];
    }

    static JsonValue collapseArray(Object parsedValue) {
        // поведение консистентно с Jackson
        if (parsedValue instanceof char[]) {
            // https://www.javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/2.8
            // .4/com/fasterxml/jackson/databind/ser/std/StdArraySerializers.CharArraySerializer.html
            return Json.createValue(new String(((char[]) parsedValue)));
        }
        if (parsedValue instanceof byte[]) {
            // https://www.javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/2.8
            // .4/com/fasterxml/jackson/databind/ser/std/ByteArraySerializer.html
            return Json.createValue(new String(Base64.getEncoder().encode((byte[]) parsedValue)));
        }
        throw new RuntimeException(String.format("Can collapse byte[] or char[] only, have %s", parsedValue));
    }

    static Class getTerminalClass(Object terminalObject) {
        if (terminalObject == null) {
            return NullClass.class;
        }
        if (JsonArrayBuilder.class.isAssignableFrom(terminalObject.getClass())) {
            return JsonArrayBuilder.class;
        }
        if (JsonObjectBuilder.class.isAssignableFrom(terminalObject.getClass())) {
            return JsonObjectBuilder.class;
        }
        if (JsonValue.class.isAssignableFrom(terminalObject.getClass())) {
            return JsonValue.class;
        }
        return terminalObject.getClass();
    }

    static boolean isPureValue(Object inputObject) {
        return TERMINAL_CLASSES.stream().anyMatch(cl -> cl.isAssignableFrom(inputObject.getClass()));
    }

    static boolean isTerminalClass(Class<?> someClass) {
        return TERMINAL_CLASSES.contains(someClass);
    }

    // маркерные классы для списка обработчиков выше
    private static class NullClass {

    }

    private static class Collapsable {

    }
}



