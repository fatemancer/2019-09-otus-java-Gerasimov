package ru.otus.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.otus.json.data.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public class SerializerTest {

    private SoftAssertions softly = new SoftAssertions();
    private MyMapper myMapper = new MyMapper();
    private ObjectMapper jacksonMapper = new ObjectMapper();

    @AfterEach
    void tearDown() {
        softly.assertAll();
    }

    @Test
    void simpleTest() {
        var testClass = new SimpleClass(1, 1.0f, 2.0d, 'a', "st1");
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void simpleObjectTest() {
        var testClass = new SimpleObjectClass(1, 1.0f, 2.0d, 'a', "st1", BigInteger
                .valueOf(Long.MAX_VALUE).add(BigInteger.ONE), BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE));
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void emptyTest() {
        var testClass = new SimpleClass();
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void emptyObjectTest() {
        var testClass = new SimpleObjectClass();
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void nullTest() {
        SimpleClass testClass = null;
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void complexTest() {
        var complexClass = new ComplexClass();
        softly.assertThat(myMapper.convert(complexClass)).isEqualTo(jacksonWrite(complexClass));
    }

    @Test
    void simpleArray() {
        var testClass = new SimpleClass[]{new SimpleClass(1, 1.0f, 2.0d, 'a', "st1"), new SimpleClass(
                2,
                1.0f,
                2.0d,
                'a',
                "st1"
        )};
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void collections() {
        var testClass = new CollectionClass();
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void simpleCollection() {
        var testClass = Set.of(new SimpleClass(1, 1.0f, 2.0d, 'a', "st1"), new SimpleClass(2, 1.0f, 2.0d, 'a', "st1"));
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void strangeTypes() {
        var testClass = new StrangeClass();
        softly.assertThat(myMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    void inheritanceTest() {
        var inheritingMapper = new MyMapper(false, true);
        var testClass = new SimpleInheritingClass();
        softly.assertThat(inheritingMapper.convert(testClass)).isEqualTo(jacksonWrite(testClass));
    }

    @Test
    @SneakyThrows
    void privateFieldsTest() {
        MyMapper privateAwareMapper = new MyMapper(false, false);
        ObjectMapper sameJacksonMapper = new ObjectMapper().setVisibility(
                PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.ANY
        );

        var testClass = new PrivateFieldClass();
        softly.assertThat(privateAwareMapper.convert(testClass)).isEqualTo(sameJacksonMapper.writeValueAsString(
                testClass));
    }

    @SneakyThrows
    private String jacksonWrite(Object o) {
        return jacksonMapper.writeValueAsString(o);
    }
}
