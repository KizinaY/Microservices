package com.kizina.resourceprocessor.service;

import com.kizina.resourceprocessor.entity.Metadata;
import com.kizina.resourceprocessor.service.exception.CannotExtractMetadataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractMetadataService {

    private final Mp3Parser parser;

    public Metadata extractMetadata(byte[] audioData) {
        org.apache.tika.metadata.Metadata tikaMetadata = extractTikaMetadata(audioData);
        return mapTikaMetadataToMetadata(tikaMetadata);
    }

    private org.apache.tika.metadata.Metadata extractTikaMetadata(byte[] audioData) {
        BodyContentHandler handler = new BodyContentHandler();
        ParseContext parseContext = new ParseContext();
        org.apache.tika.metadata.Metadata tikaMetadata = new org.apache.tika.metadata.Metadata();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData);
        try {
            parser.parse(inputStream, handler, tikaMetadata, parseContext);
        } catch (IOException | SAXException | TikaException e) {
            log.error("Cannot Extract Metadata. " + e);
            throw new CannotExtractMetadataException(e);
        }
        return tikaMetadata;
    }

    private Metadata mapTikaMetadataToMetadata(org.apache.tika.metadata.Metadata tikaMetadata) {
        String title = tikaMetadata.get("dc:title");
        String artist = tikaMetadata.get("xmpDM:artist");
        String album = tikaMetadata.get("xmpDM:album");
        String duration = tikaMetadata.get("xmpDM:duration");
        String year = tikaMetadata.get("xmpDM:releaseDate");
        return Metadata.builder()
                .title(title)
                .artist(artist)
                .album(album)
                .duration(duration)
                .songYear(year)
                .build();
    }
}
