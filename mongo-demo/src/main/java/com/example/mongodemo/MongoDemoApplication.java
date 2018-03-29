package com.example.mongodemo;

import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
public class MongoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoDemoApplication.class, args);
    }
}

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/files")
class FileController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    private static Query getFilenameQuery(String name) {
        return Query.query(GridFsCriteria.whereFilename().is(name));
    }

    @PostMapping
    String createOrUpdate(@RequestParam MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        maybeLoadFile(name).ifPresent(
                p -> gridFsTemplate.delete(getFilenameQuery(name))
        );
        gridFsTemplate.store(file.getInputStream(), name, file.getContentType())
                .save();
        return "redirect:/";
    }

    private Optional<GridFSDBFile> maybeLoadFile(String name) {
        GridFSDBFile file = gridFsTemplate.findOne(getFilenameQuery(name));
        return Optional.ofNullable(file);
    }

    @GetMapping
    @ResponseBody
    List<String> list() {
        return getFiles().stream()
                .map(GridFSDBFile::getFilename)
                .collect(toList());
    }

    private List<GridFSDBFile> getFiles() {
        return gridFsTemplate.find(null);
    }

    @GetMapping("/{name:.+}")
    ResponseEntity<?> get(@PathVariable String name) throws Exception {
        Optional<GridFSDBFile> optionalCreated = maybeLoadFile(name);
        if (optionalCreated.isPresent()) {
            GridFSDBFile created = optionalCreated.get();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                created.writeTo(os);
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(created.getContentType()))
                        .body(os.toByteArray());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

