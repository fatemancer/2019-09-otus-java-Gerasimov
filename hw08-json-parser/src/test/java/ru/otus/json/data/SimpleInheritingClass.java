package ru.otus.json.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleInheritingClass extends SimpleClass {
    String childString;
}
