package com.thuverx.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.thuverx.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface PackExtractor {
    default <T> List<T> getParsedJsons(String wildcard, Class<T> clazz) {
        List<Path> files = getFiles(wildcard);
        List<T> clazzes = new ArrayList<>();

        for(Path file : files) {
            try {
                JsonReader jsonReader = new JsonReader(new StringReader(Files.readString(file)));
                jsonReader.setLenient(true);

                clazzes.add(Constants.GSON.fromJson(JsonParser.parseReader(jsonReader), clazz));

                jsonReader.close();
            } catch (JsonParseException | IOException e) {
                Constants.LOG.error("Failed to parse " + file + " as " + clazz.getSimpleName());
                Constants.LOG.error(e.getMessage());
            }
        }

        return clazzes;
    }

    default <T> T getParsedJson(String wildcard, Class<T> clazz) {
        try {
            Path file = getFile(wildcard);

            JsonReader jsonReader = new JsonReader(new StringReader(Files.readString(file)));
            jsonReader.setLenient(true);

            JsonElement json = JsonParser.parseReader(jsonReader);

            jsonReader.close();

            return Constants.GSON.fromJson(json, clazz);

        } catch (Exception e) {
            throw new RuntimeException(clazz.getName(),e);
        }
    }

    Path getFile(String wildcard);
    List<Path> getFiles(String wildcard);
}
