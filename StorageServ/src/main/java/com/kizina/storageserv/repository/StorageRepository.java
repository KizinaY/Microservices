package com.kizina.storageserv.repository;

import com.kizina.storageserv.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    Storage findByStorageType(String storageType);
}
