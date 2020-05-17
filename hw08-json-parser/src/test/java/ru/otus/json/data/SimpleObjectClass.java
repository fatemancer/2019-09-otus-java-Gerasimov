package ru.otus.json.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleObjectClass {
    Integer field1;;
    Float field2;
    Double field3;
    Character field4;
    String field5;
    BigInteger field6;
    BigDecimal field7;
}
