package ru.otus;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Config {

    private static final String MIN_MEMORY = "64m";
    private static final String MAX_MEMORY = "8192m";
    static final String STAT_FILE = String.format("%s_%s_total.json", MIN_MEMORY, MAX_MEMORY);

    private static final String CURRENT_PATH = new File(".").getAbsolutePath();
    private static final String SYSTEM_CLASSPATH = System.getProperty("java.class.path");

    private Map<String, String> gcParams;

    Config() {
        gcParams = Map.of(
                "serial", "-XX:+UseSerialGC",
                "parallel", "-XX:+UseParallelGC",
                "CMS", "-XX:+UseConcMarkSweepGC",
                "G1", "-XX:+UseG1GC",
                "ZGC", "-XX:+UseZGC"
        );
    }

    List<List<String>> getVM() {
        String javaHome = System.getProperty("java.home");
        String javaBin = String.format("%s%sbin%sjava", javaHome, File.separator, File.separator);
        String classpath = CURRENT_PATH + ":" + SYSTEM_CLASSPATH;
        String className = "ru.otus.Test";
        return gcParams.entrySet()
                .stream()
                .map((entry) -> List.of(
                        javaBin,
                        "-cp",
                        classpath,
                        "-Xmx" + MAX_MEMORY,
                        "-Xms" + MIN_MEMORY,
                        "-XX:+UnlockExperimentalVMOptions",
                        entry.getValue(),
                        className,
                        entry.getKey()
                ))
                .collect(Collectors.toList());
    }
}
