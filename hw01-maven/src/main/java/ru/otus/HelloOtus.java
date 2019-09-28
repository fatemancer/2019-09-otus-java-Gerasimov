package ru.otus;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.google.common.io.Resources.getResource;

@SuppressWarnings("UnstableApiUsage")
public class HelloOtus {

    public static void main(String[] args) {

        try {
            String characters = Resources.asCharSource(
                    getResource("EXAMPLE.md"), StandardCharsets.UTF_8
            ).read();

            HashMap<Character, Integer> frequencies = new HashMap<>();
            characters.chars()
                    .mapToObj(c -> (char) c)
                    .forEach(c -> frequencies.merge(c, 1, Integer::sum));

            System.out.println(
                    String.format("Letter frequencies in EXAMPLE.md: %s. \n File is read using Guava!", frequencies)
            );
        } catch (Exception e) {
            System.out.println("Wanted to read EXAMPLE.md using Guava but failed :(");
        }
    }
}
