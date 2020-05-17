package ru.otus.json;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

class TypeHelper {

    static final Map<Class, Function<Object, String>> TERMINAL_CONVERSION_MAP = Map.of(
            NullClass.class, (o) -> JsonValue.NULL.toString(),
            JsonArrayBuilder.class, (o) -> ((JsonArrayBuilder) o).build().toString(),
            JsonObjectBuilder.class, (o) -> ((JsonObjectBuilder) o).build().toString()
    );

    static final Map<Class, Function<Object, JsonValue>> TERMINAL_VALUES_HANDLER_MAP = Map.ofEntries(
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
            Map.entry(Collapsable.class, TypeHelper::collapseArray)
    );

    @SuppressWarnings("unchecked")
    static Stream<?> getStreamOf(Object inputObject) {

        if (inputObject instanceof Collection) {
            return ((Collection<Object>) inputObject).stream();
        }

        if (inputObject instanceof int[]) {
            return Arrays.stream((int[]) inputObject).boxed();
        }
        if (inputObject instanceof long[]) {
            return Arrays.stream((long[]) inputObject).boxed();
        }
        if (inputObject instanceof char[]) {
            char[] charArray = (char[]) inputObject;
            return CharBuffer.wrap(charArray).chars().boxed();
        }
        if (inputObject instanceof byte[]) {
            byte[] byteArray = (byte[]) inputObject;
            IntBuffer intBuffer = ByteBuffer.wrap(byteArray).asIntBuffer();
            int[] intArray = new int[intBuffer.capacity()];
            intBuffer.get(intArray);
            return Arrays.stream(intArray).boxed();
        }
        if (inputObject instanceof float[]) {
            float[] floatArray = (float[]) inputObject;
            return Stream.of(floatArray);
        }
        if (inputObject instanceof double[]) {
            return Arrays.stream((double[]) inputObject).boxed();
        }
        return Arrays.stream((Object[]) inputObject);
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

    private static boolean isCollection(Object o) {
        return Collection.class.isAssignableFrom(o.getClass());
    }

    private static boolean isArray(Object o) {
        return o.getClass().isArray();
    }

    static boolean isArrayOrCollection(Object inputObject) {
        return isArray(inputObject) || isCollection(inputObject);
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
        } else {
            return terminalObject.getClass();
        }
    }

    // маркерные классы для списка обработчиков выше
    private static class NullClass {

    }

    private static class Collapsable {

    }
}



