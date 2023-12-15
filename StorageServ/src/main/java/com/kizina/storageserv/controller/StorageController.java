package com.kizina.storageserv.controller;

import com.kizina.storageserv.entity.Storage;
import com.kizina.storageserv.service.StorageService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("storage")
@AllArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("")
    public ResponseEntity<Long> saveStorage(@RequestBody Storage storage) {
        Storage savedStorage = storageService.saveStorage(storage);
        return ResponseEntity.ok(savedStorage.getId());
    }

    @GetMapping("")
    public ResponseEntity<List<Storage>> getAllStorages() {
        List<Storage> allStorages = storageService.findAllStorages();
        return ResponseEntity.ok(allStorages);
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, List<Long>>> deleteStoragesByIds(@RequestParam("id") @Size(max = 200) @Parameter(description = "Ids list no longer than 200 chars") List<String> ids) {
        List<Long> idLongList = convertStringListToLongList(ids);
        List<Long> deletedIds = storageService.deleteStoragesByIds(idLongList);
        Map<String, List<Long>> response = Collections.singletonMap("ids", deletedIds);
        return ResponseEntity.ok(response);
    }

    private static List<Long> convertStringListToLongList(List<String> ids) {
        return ids.stream()
                .map(Long::valueOf)
                .toList();
    }

}
