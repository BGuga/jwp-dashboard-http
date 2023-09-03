package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourceProvider {

    public boolean haveResource(String resourcePath) {
        return findFileURL(resourcePath).isPresent();
    }

    private Optional<URL> findFileURL(String resourcePath) {
        return Optional.ofNullable(getClass().getClassLoader().getResource("static" + resourcePath));
    }

    public String resourceBodyOf(String resourcePath) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(getFile(resourcePath)))) {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> lines = bufferedReader.lines()
                .collect(Collectors.toList());
            for (String line : lines) {
                stringBuilder.append(line);
                stringBuilder.append("\r\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String resourcePath) {
        try {
            return new File(
                findFileURL(resourcePath).orElseThrow((() -> new IllegalArgumentException("파일이 존재하지 않습니다.")))
                    .toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI 문법이 잘못 되었습니다.", e);
        }

    }

    public String contentTypeOf(String resourcePath) {
        File file = getFile(resourcePath);
        String fileName = file.getName();
        if (fileName.endsWith(".js")) {
            return "Content-Type: text/javascript ";
        }
        if (fileName.endsWith(".css")) {
            return "Content-Type: text/css ";
        }
        if (fileName.endsWith(".html")) {
            return "Content-Type: text/html;charset=utf-8 ";
        }
        return "text/plain";
    }
}