package com.kizina.songservice.service;

import com.kizina.songservice.entity.Metadata;
import com.kizina.songservice.repository.MetadataRepository;
import com.kizina.songservice.service.exception.NoSuchMetadataException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MetadataService {

    private final MetadataRepository metadataRepository;

    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public Metadata saveMetadata(Metadata metadata) {
        return metadataRepository.save(metadata);
    }

    public List<Long> deleteMetadataByIds(List<Long> ids) {
        metadataRepository.deleteAllById(ids);
        return ids;
    }

    public Metadata getMetadataById(Long id) {
        Optional<Metadata> metadata = metadataRepository.findById(id);
        if (metadata.isPresent()) {
            return metadata.get();
        } else {
            throw new NoSuchMetadataException(id);
        }
    }

}
