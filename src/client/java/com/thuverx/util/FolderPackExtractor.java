package com.thuverx.util;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FolderPackExtractor implements PackExtractor {
    private final Path path;
    public FolderPackExtractor(Path path) {
        this.path = path;
    }


    @Override
    public Path getFile(String wildcard) {
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);

            final Path[] pathResult = {null};
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes basicFileAttributesattrs) {
                    if (matcher.matches(path.relativize(file))) {
                        pathResult[0] = file;
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return pathResult[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Path> getFiles(String wildcard) {
        try {
            List<Path> matchedFiles = new ArrayList<>();
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);

            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes basicFileAttributesattrs) {
                    if (matcher.matches(path.relativize(file))) {
                        matchedFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return matchedFiles;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
