package com.kizina.songservice.service;

import com.kizina.songservice.entity.Metadata;
import com.kizina.songservice.service.exception.NoSuchMetadataException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MetadataServiceTest {

    @Autowired
    private MetadataService metadataService;

    @Test
    void shouldSaveWhenNewMetadata() {
        Metadata testMetadata = new Metadata(null, "Test Song 4", "Test Artist 4", "Test Album 4", "3:15", "2000", 26799L);
        Metadata savedMetadata = metadataService.saveMetadata(testMetadata);
        assertEquals(testMetadata.getTitle(), savedMetadata.getTitle());
        assertEquals(testMetadata.getArtist(), savedMetadata.getArtist());
        assertEquals(testMetadata.getAlbum(), savedMetadata.getAlbum());
        assertEquals(testMetadata.getDuration(), savedMetadata.getDuration());
        assertEquals(testMetadata.getSongYear(), savedMetadata.getSongYear());
        assertEquals(testMetadata.getResourceId(), savedMetadata.getResourceId());
        assertNotNull(savedMetadata.getId());
    }

    @Test
    void shouldThrowExceptionWhenTryToSaveExistingMetadata() {
        Metadata testMetadata = new Metadata(null, "Test Song 1", "Test Artist 1", "Test Album 1", "3:30", "2023", 12345L);
        assertThrows(DataIntegrityViolationException.class, () -> metadataService.saveMetadata(testMetadata));
    }

    @Test
    void shouldGetMetadataWhenItExists() {
        Metadata expectedMetadata = new Metadata(1L, "Test Song 1", "Test Artist 1", "Test Album 1", "3:30", "2023", 12345L);
        Metadata metadataInDB = metadataService.getMetadataById(1L);
        assertEquals(expectedMetadata, metadataInDB);
    }

    @Test
    void shouldThrowExceptionWhenTryToGetNotExistingMetadata() {
        assertThrows(NoSuchMetadataException.class, () -> metadataService.getMetadataById(9999L));
    }

    @Test
    void shouldDeleteMetadataWhenItExists() {
        Metadata expectedMetadata = new Metadata(2L, "Test Song 2", "Test Artist 2", "Test Album 2", "4:15", "2022", 67890L);
        Metadata metadataInDB = metadataService.getMetadataById(2L);
        assertEquals(expectedMetadata, metadataInDB);
        metadataService.deleteMetadataByIds(List.of(2L));
        assertThrows(NoSuchMetadataException.class, () -> metadataService.getMetadataById(2L));
    }
}