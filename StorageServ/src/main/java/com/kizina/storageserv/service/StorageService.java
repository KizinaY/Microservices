package com.kizina.storageserv.service;

import com.kizina.storageserv.entity.Storage;
import com.kizina.storageserv.repository.StorageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    public Storage saveStorage(Storage storage) {
        return storageRepository.save(storage);
    }

    public List<Storage> findAllStorages() {
        return storageRepository.findAll();
    }

    public Storage findByType(String storageType){
        return storageRepository.findByStorageType(storageType);
    }

    public List<Long> deleteStoragesByIds(List<Long> ids) {
        storageRepository.deleteAllById(ids);
        return ids;
    }
}
