package com.kizina.resourceprocessor.service;

import com.kizina.resourceprocessor.entity.Metadata;
import com.kizina.resourceprocessor.service.exception.CannotExtractMetadataException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtractMetadataServiceUnitTest {

    @Mock
    private Mp3Parser mp3Parser;

    @InjectMocks
    private ExtractMetadataService extractMetadataService;

    @Test
    void shouldExtractMetadataWhenFileIsValid() throws TikaException, IOException, SAXException {
        byte[] audioData = readResourceAsBytes("/audio/sample-3s.mp3");
        doCallRealMethod()
                .when(mp3Parser)
                .parse(any(ByteArrayInputStream.class), any(BodyContentHandler.class), any(org.apache.tika.metadata.Metadata.class), any(ParseContext.class));

        Metadata expectedMetadata = Metadata.builder()
                .title("Sint3S")
                .artist("Sint")
                .album("Three")
                .duration("3.265310287475586")
                .songYear("2021")
                .build();

        Metadata actualMetadata = extractMetadataService.extractMetadata(audioData);
        assertEquals(expectedMetadata, actualMetadata);
    }

    @Test
    public void shouldThrowExceptionWhenFileIsNotValid() throws IOException, SAXException, TikaException {
        byte[] audioData = new byte[0];
        doThrow(new TikaException("Test error"))
                .when(mp3Parser)
                .parse(any(ByteArrayInputStream.class), any(BodyContentHandler.class), any(org.apache.tika.metadata.Metadata.class), any(ParseContext.class));

        CannotExtractMetadataException exception = assertThrows(CannotExtractMetadataException.class, () -> {
            extractMetadataService.extractMetadata(audioData);
        });
        assertEquals(TikaException.class.getName() + ": Test error", exception.getMessage());
    }

    private byte[] readResourceAsBytes(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            byte[] buffer = new byte[inputStream.available()];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Error reading resource: " + resourcePath);
            }
            return buffer;
        }
    }
}