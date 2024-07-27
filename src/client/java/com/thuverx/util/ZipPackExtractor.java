package com.thuverx.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZipPackExtractor implements PackExtractor {
    private final Path path;
    public ZipPackExtractor(Path path) {
        this.path = path;
    }
    @Override
    public Path getFile(String wildcard) {
        try {
            FileSystem zipFs = FileSystems.newFileSystem(path, Collections.emptyMap());
            PathMatcher matcher = zipFs.getPathMatcher("glob:" + wildcard);

            final Path[] pathResult = {null};

            for (Path root : zipFs.getRootDirectories()) {
                Files.walkFileTree(root, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributesattrs) {
                        if (matcher.matches(root.relativize(path))) {
                            pathResult[0] = path;
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            return pathResult[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Path> getFiles(String wildcard) {
        try {
            FileSystem zipFs = FileSystems.newFileSystem(path, Collections.emptyMap());
            PathMatcher matcher = zipFs.getPathMatcher("glob:" + wildcard);
            List<Path> matchedFiles = new ArrayList<>();

            for (Path root : zipFs.getRootDirectories()) {
                Files.walkFileTree(root, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes basicFileAttributesattrs) {
                        if (matcher.matches(root.relativize(file))) {
                            matchedFiles.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            return matchedFiles;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
