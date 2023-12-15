package com.kizina.songservice.controller;

import com.kizina.songservice.entity.Metadata;
import com.kizina.songservice.service.MetadataService;
import com.kizina.songservice.service.exception.NoSuchMetadataException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Validated
@RestController
@RequestMapping("songs")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping("")
    public ResponseEntity<Long> saveMetadata(@RequestBody Metadata metadata) {
        Metadata savedMetadata = metadataService.saveMetadata(metadata);
        return ResponseEntity.ok(savedMetadata.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Metadata> getMetadataById(@PathVariable Long id) {
        Metadata metadata = metadataService.getMetadataById(id);
        return ResponseEntity.ok(metadata);
    }

    @ExceptionHandler(NoSuchMetadataException.class)
    public ResponseEntity<String> handleNoSuchMetadataCase(NoSuchMetadataException exception) {
        Long metadataId = exception.getMetadataId();
        return ResponseEntity.status(NOT_FOUND).body("The song metadata with the specified id " + metadataId + " does not exist");
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, List<Long>>> deleteMetadataByIds(@RequestParam("id") @Size(max = 200) @Parameter(description = "Ids list no longer than 200 chars") List<String> ids) {
        List<Long> idLongList = convertStringListToLongList(ids);
        List<Long> deletedIds = metadataService.deleteMetadataByIds(idLongList);
        Map<String, List<Long>> response = Collections.singletonMap("ids", deletedIds);
        return ResponseEntity.ok(response);
    }

    private static List<Long> convertStringListToLongList(List<String> ids) {
        return ids.stream()
                .map(Long::valueOf)
                .toList();
    }

}
