package ru.otus;

import lombok.extern.slf4j.Slf4j;
import ru.otus.data.Aggregator;
import ru.otus.data.VMLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Main {

    private static final String TEST_CLASS = "ru.otus.Test";

    public static void main(String[] args) throws Exception {

        Config config = new Config();
        initTestClass();
        config.getVM().forEach((element) -> {
            try {
                launchVM(element);
            } catch (Exception e) {
                log.error("Failed to start VM {}", element);
            }
        });
        List<VMLog> vmLog = parseRawData();
        vmLog.forEach(log -> {
            System.out.println(String.format("VM with GC: %s. \n Mean duration of GC: %s ms \n Median duration of GC:" +
                            " %s ms" + "\n Last processed index %s, %s ms total\n",
                    log.getVmName(),
                    log.getMeanGcDuration(),
                    log.getMedianGcDuration(),
                    log.getLastProcessedIndex(),
                    log.getTotalTime()
            ));
        });
    }

    private static List<VMLog> parseRawData() {
        String file = Config.STAT_FILE;
        try (Stream<String> strings = Files.lines(Paths.get(file))) {
            return strings.map(Aggregator::from)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Cannot parse {}", file);
            return List.of();
        }
    }

    private static void initTestClass() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("javac", TEST_CLASS);
        Process compilation = processBuilder.start();
        compilation.waitFor();
        System.out.println("Successfully compiled test class");
        Files.writeString(Path.of(Config.STAT_FILE), "", StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private static void launchVM(List<String> params) throws Exception {

        ProcessBuilder processBuilder = new ProcessBuilder(params);
        String type = params.get(params.size() - 1);

        System.out.println(type + " VM started as: " + params);

        Process process =
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start();
        process.waitFor();

        System.out.println(type + " VM dead. GC stat dumped");
    }

}