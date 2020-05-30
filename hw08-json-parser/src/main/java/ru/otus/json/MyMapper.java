package ru.otus.json;

import org.apache.commons.lang3.ArrayUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MyMapper {

    // Цель: Научиться сериализовывать объект в json, попрактиковаться в разборе структуры объекта.
    // Напишите свой json object writer (object to JSON string) аналогичный gson на основе javax.json.
    //
    // Поддержите:
    // - массивы объектов и примитивных типов
    // - коллекции из стандартный библиотеки.

    private boolean publicOnly = false;
    private boolean serializeInherited = false;

    public MyMapper(boolean publicOnly, boolean serializeInherited) {
        this.publicOnly = publicOnly;
        this.serializeInherited = serializeInherited;
    }

    public MyMapper() {
    }

    private BiConsumer<Object, JsonArrayBuilder> collectionMapper = (Object input, JsonArrayBuilder builder) -> {
        ((Collection) input).forEach(o -> processElement(o, builder));
    };

    private BiConsumer<Object, JsonArrayBuilder> arrayMapper = (Object input, JsonArrayBuilder builder) -> {
        for (int i = 0; i < Array.getLength(input); i++) {
            Object o = Array.get(input, i);
            processElement(o, builder);
        }
    };

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
            builder.add(f.getName(), processObject(getFieldValue(inputObject, f)));
        }
    };

    private Function<Object, JsonValue> defaultValueConverter = (Object input) -> {

        if (TypeHelper.isArray(input)) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            arrayMapper.accept(input, arrayBuilder);
            return arrayBuilder.build();
        } else if (TypeHelper.isCollection(input)) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            collectionMapper.accept(input, arrayBuilder);
            return arrayBuilder.build();
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            defaultObjectMapper.accept(input, objectBuilder);
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
        return TypeHelper.getOutputValueHandler(rootObject, o -> {
            throw new RuntimeException(String.format(
                    "Serializer must return nullable builder or pure JsonValue, returned %s of class %s",
                    rootObject,
                    rootObject.getClass()
            ));
        }).apply(rootObject);
    }

    private Object internalConvert(Object inputObject) {
        if (inputObject == null) {
            return null;
        } else if (TypeHelper.isByteOrCharArray(inputObject)) {
            return TypeHelper.collapseArray(inputObject);
        } else if (TypeHelper.isPureValue(inputObject)) {
            return processObject(inputObject);
        } else if (TypeHelper.isArray(inputObject)) {
            JsonArrayBuilder rootBuilder = Json.createArrayBuilder();
            arrayMapper.accept(inputObject, rootBuilder);
            return rootBuilder;
        } else if (TypeHelper.isCollection(inputObject)) {
            JsonArrayBuilder rootBuilder = Json.createArrayBuilder();
            collectionMapper.accept(inputObject, rootBuilder);
            return rootBuilder;
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            defaultObjectMapper.accept(inputObject, objectBuilder);
            return objectBuilder;
        }
    }

    private void processElement(Object inputObject, JsonArrayBuilder arrayBuilder) {
        Class<?> supportedClass = TypeHelper.getSupportedClass(inputObject);
        if (TypeHelper.isTerminalClass(supportedClass)) {
            arrayBuilder.add(processObject(inputObject));
        } else {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            defaultObjectMapper.accept(inputObject, objectBuilder);
            arrayBuilder.add(objectBuilder);
        }
    }

    private JsonValue processObject(Object parsedValue) {
        return TypeHelper.getValueHandler(
                TypeHelper.getSupportedClass(parsedValue), defaultValueConverter
        ).apply(parsedValue);
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
