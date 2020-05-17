package ru.otus.json;

import org.apache.commons.lang3.ArrayUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MyMapper {

    //Цель: Научиться сериализовывать объект в json, попрактиковаться в разборе структуры объекта.
    //Напишите свой json object writer (object to JSON string) аналогичный gson на основе javax.json.
    //
    //Поддержите:
    //- массивы объектов и примитивных типов
    //- коллекции из стандартный библиотеки.

    // org.glassfish.json.MapUtil.handle
    private static Set<Class> TERMINAL_CLASSES = Set.of(
            BigDecimal.class,
            BigInteger.class,
            Boolean.class,
            Double.class,
            Integer.class,
            Long.class,
            String.class
    );
    private boolean publicOnly = false;
    private boolean serializeInherited = false;

    public MyMapper(boolean publicOnly, boolean serializeInherited) {
        this.publicOnly = publicOnly;
        this.serializeInherited = serializeInherited;
    }

    public MyMapper() {
    }

    private BiConsumer<Object, JsonObjectBuilder> defaultObjectMapper = (
            Object inputObject,
            JsonObjectBuilder builder
    ) -> {
        Field[] fields = inputObject.getClass().getDeclaredFields();
        if (serializeInherited) {
            Field[] superFields = inputObject.getClass().getSuperclass().getDeclaredFields();
            fields = ArrayUtils.addAll(superFields, fields);
        }
        for (Field f : fields) {
            if (publicOnly && Modifier.isPrivate(f.getModifiers())) {
                continue;
            }
            builder.add(f.getName(), valueFromField(f, inputObject));
        }
    };

    private BiConsumer<Object, JsonArrayBuilder> defaultArrayMapper = (
            Object inputObject,
            JsonArrayBuilder builder
    ) -> {
        List<?> streamOf = TypeHelper.getStreamOf(inputObject).collect(Collectors.toList());
        streamOf.forEach(o -> process(o, builder));
    };

    private Function<Object, JsonValue> defaultValueConverter = (Object input) -> {

        if (TypeHelper.isArrayOrCollection(input)) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            defaultArrayMapper.accept(input, arrayBuilder);
            return arrayBuilder.build();
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            process(input, objectBuilder);
            return objectBuilder.build();
        }
    };

    /**
     * Превращает входящий объект в JSON
     *
     * @param inputObject POJO
     * @return строковое представление. Поля в том порядке, как объявлены в объекте
     */
    public String convert(Object inputObject) {
        Object rootObject = internalConvert(inputObject);
        return TypeHelper.TERMINAL_CONVERSION_MAP.getOrDefault(TypeHelper.getTerminalClass(rootObject), (o -> {
            throw new RuntimeException(String.format(
                    "Serializer must return nullable builder, returned %s of class %s",
                    rootObject,
                    rootObject.getClass()
            ));
        })).apply(rootObject);
    }

    private JsonValue valueFromField(Field f, Object inputObject) {
        return toValue(getFieldValue(inputObject, f));
    }

    private Object internalConvert(Object inputObject) {
        if (inputObject == null) {
            return null;
        } else if (TypeHelper.isArrayOrCollection(inputObject)) {
            JsonArrayBuilder rootBuilder = Json.createArrayBuilder();
            defaultArrayMapper.accept(inputObject, rootBuilder);
            return rootBuilder;
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            process(inputObject, objectBuilder);
            return objectBuilder;
        }
    }

    private JsonValue toValue(Object parsedValue) {

        return TypeHelper.TERMINAL_VALUES_HANDLER_MAP.getOrDefault(
                TypeHelper.getSupportedClass(parsedValue),
                defaultValueConverter
        ).apply(parsedValue);
    }

    private void process(Object inputObject, JsonArrayBuilder arrayBuilder) {
        Class<?> supportedClass = TypeHelper.getSupportedClass(inputObject);
        if (TERMINAL_CLASSES.contains(supportedClass)) {
            arrayBuilder.add(toValue(inputObject));
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            defaultObjectMapper.accept(inputObject, objectBuilder);
            arrayBuilder.add(objectBuilder);
        }
    }

    private void process(Object inputObject, JsonObjectBuilder objectBuilder) {
        defaultObjectMapper.accept(inputObject, objectBuilder);
    }

    private Object getFieldValue(Object o, Field f) {
        f.setAccessible(true);
        try {
            return f.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Error parsing field %s of object %s", f, o), e);
        }
    }

}
