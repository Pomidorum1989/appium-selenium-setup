package org.dorum.automation.common.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DataUtils {

    public static void saveTextToFile(String text) {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            fileWriter = new FileWriter("PAGESOURCE.html");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e) {
            Log.warn("FAILED - save text to file\n%s", e);
        }
    }

    @SneakyThrows
    public static void writeToFile(String fileAddress, String fileContext, boolean isAppend) {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileAddress, isAppend));
        bufferedWriter.write(fileContext + System.lineSeparator());
        bufferedWriter.close();
        Log.info("Text information was added to the file: %s", fileAddress);
    }

    @SneakyThrows
    public static File writeToFile(String fileContext, String fileType) {
        File file = Files.createTempFile("temp", fileType).toFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
        bufferedWriter.write(fileContext + System.lineSeparator());
        bufferedWriter.close();
        Log.info("Text information was added to the file");
        return file;
    }

    @SneakyThrows
    public static void clearTheFile(String fileAddress) {
        FileWriter fwOb = new FileWriter(fileAddress, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
        Log.info("File was cleared: %s", fileAddress);
    }

    @SneakyThrows
    public static void overwriteLine(Path filePath, String newLine, int lineNumber) {
        List<String> lines = Files.readAllLines(filePath);
        if (lineNumber < 0 || lineNumber >= lines.size()) {
            throw new IllegalArgumentException("Invalid line number: " + lineNumber);
        }
        lines.set(lineNumber, newLine);
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    public static int findLineNumber(String filePath, String searchValue) {
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchValue)) {
                    return lineNumber;
                }
            }
        } catch (Exception e) {
          Log.info("", e);
        }
        return -1;
    }

    public static void overWritePrivateVariable(String className, String fieldName, Object value) {
        Class<?> externalClass = null;
        try {
            externalClass = Class.forName(className);
        } catch (Exception e) {
            Log.warn("FAILED - unable to find class\n%s", e);
        }
        Field field = null;
        try {
            assert externalClass != null;
            field = externalClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            Log.simpleInfo(String.format( "Overwrite field %s to value %s", fieldName, value));
        } catch (Exception e) {
            Log.warn("FAILED - unable to find field %s\n%s", field, e);
        }
    }

    @SneakyThrows
    public static File writeToCSV(List<String[]> data) {
        File file = Files.createTempFile("temp", "csv").toFile();
        Writer writer = new FileWriter(file);
        for (String[] row : data) {
            writer.write(String.join(",", row));
            writer.write("\n");
        }
        writer.close();
        return file;
    }

    public static File creteFileFromBytes(byte[] bytes, String path) {
        File file = new File(path);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
            Log.info("File is created in the location: %s", path);
        } catch (Exception e) {
            Log.warn("FAILED - unable to create the file: %s\n%s", path, e);
        }
        return file;
    }

    @SneakyThrows
    public static void createLogFile(String fileName) {
        Path path = Paths.get(String.format("", fileName));
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
}
