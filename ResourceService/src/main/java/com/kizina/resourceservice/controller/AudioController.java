package com.kizina.resourceservice.controller;

import com.kizina.resourceservice.controller.exception.InternalServerException;
import com.kizina.resourceservice.controller.exception.NotMp3Exception;
import com.kizina.resourceservice.producer.RabbitMQProducer;
import com.kizina.resourceservice.service.AudioService;
import com.kizina.resourceservice.service.exception.CannotUploadFileToBucket;
import com.kizina.resourceservice.service.exception.NoSuchAudioException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Validated
@RestController
@RequestMapping("resources")
public class AudioController {

    private static final String PERMANENT_STORAGE_TYPE = "PERMANENT";
    private static final String STAGING_STORAGE_TYPE = "STAGING";
    private final AudioService audioService;
    private final RabbitMQProducer rabbitMQProducer;

    public AudioController(AudioService audioService, RabbitMQProducer rabbitMQProducer) {
        this.audioService = audioService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAudioById(@PathVariable Long id) {
        byte[] audioBytes = audioService.getFileById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        headers.setContentDispositionFormData("attachment", "audio.mp3");
        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> uploadAudio(@RequestPart("file") MultipartFile audioFile) {
        if (!"audio/mpeg".equals(audioFile.getContentType())) {
            throw new NotMp3Exception();
        }
        Long savedId = audioService.uploadToSuitableStorage(audioFile, STAGING_STORAGE_TYPE);
        rabbitMQProducer.sendMessage(savedId.toString());
        Map<String, Long> response = Collections.singletonMap("id", savedId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String, List<Long>>> deleteAudioByIds(@RequestParam("id") @Size(max = 200) @Parameter(description = "Ids list no longer than 200 chars") List<String> ids) {
        List<Long> longIds = convertStringListToLongList(ids);
        List<Long> deletedIds = audioService.deleteAudioByIds(longIds);
        Map<String, List<Long>> response = Collections.singletonMap("ids", deletedIds);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(NoSuchAudioException.class)
    public ResponseEntity<String> handleNoSuchAudio(NoSuchAudioException exception) {
        Long audioId = exception.getAudioId();
        return ResponseEntity.status(NOT_FOUND).body("The resource with the specified id " + audioId + " does not exist");
    }

    @ExceptionHandler(CannotUploadFileToBucket.class)
    public ResponseEntity<String> handleUploadingS3Exception(CannotUploadFileToBucket exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Cannot upload file to bucket");
    }

    @ExceptionHandler(NotMp3Exception.class)
    public ResponseEntity<String> handleNoSuchAudio(NotMp3Exception exception) {
        return ResponseEntity.status(BAD_REQUEST).body("Validation failed or request body is invalid MP3");
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<String> handleInternalServerException(InternalServerException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("An internal server error has occurred");
    }

    private static List<Long> convertStringListToLongList(List<String> ids) {
        return ids.stream()
                .map(Long::valueOf)
                .toList();
    }
}
