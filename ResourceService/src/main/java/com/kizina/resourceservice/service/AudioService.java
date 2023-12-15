package com.kizina.resourceservice.service;

import com.kizina.resourceservice.entity.AudioLocation;
import com.kizina.resourceservice.entity.Storage;
import com.kizina.resourceservice.repository.AudioLocationRepository;
import com.kizina.resourceservice.service.exception.CannotUploadFileToBucket;
import com.kizina.resourceservice.service.exception.NoSuchAudioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AudioService {

    private final AudioLocationRepository audioLocationRepository;
    private final StorageService storageService;
    private final S3Client s3Client;

    public AudioService(AudioLocationRepository audioLocationRepository, StorageService storageService, S3Client s3Client) {
        this.audioLocationRepository = audioLocationRepository;
        this.storageService = storageService;
        this.s3Client = s3Client;
    }

    public Long uploadToSuitableStorage(MultipartFile audioFile, String storageType) {
        Storage storageByType = storageService.getStorageByType(storageType);
        String bucket = storageByType.getBucket();
        return uploadFile(audioFile, bucket);
    }

    public Long uploadFile(MultipartFile audioFile, String bucketName) {
        AudioLocation audioLocation = uploadFileToS3(audioFile, bucketName);
        AudioLocation savedAudioLocation = saveAudioLocation(audioLocation);
        return savedAudioLocation.getId();
    }

    private AudioLocation uploadFileToS3(MultipartFile audioFile, String bucketName) {
        try {
            String nameForSave = System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nameForSave)
                    .build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(audioFile.getInputStream(), audioFile.getSize()));
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nameForSave)
                    .build();
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            if (response == null) {
                throw new CannotUploadFileToBucket();
            }
            return AudioLocation.builder().bucketName(bucketName).fileKey(nameForSave).build();
        } catch (IOException e) {
            throw new CannotUploadFileToBucket(e);
        }
    }

    private AudioLocation saveAudioLocation(AudioLocation audioLocation) {
        return audioLocationRepository.save(audioLocation);
    }

    public byte[] getFileById(Long id) {
        AudioLocation audioLocation = findAudioLocation(id);
        return getFileFromS3(audioLocation.getBucketName(), audioLocation.getFileKey());
    }

    private byte[] getFileFromS3(String bucketName, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return objectAsBytes.asByteArray();
    }

    private AudioLocation findAudioLocation(Long id) {
        Optional<AudioLocation> audioLocation = audioLocationRepository.findById(id);
        if (audioLocation.isPresent()) {
            return audioLocation.get();
        } else {
            throw new NoSuchAudioException(id);
        }
    }

    public List<Long> deleteAudioByIds(List<Long> ids) {
        List<AudioLocation> allById = audioLocationRepository.findAllById(ids);
        if (allById.isEmpty()) {
            return Collections.emptyList();
        }
        List<ObjectIdentifier> keysToDelete = allById.stream().map(audioLocation -> {
            String fileKey = audioLocation.getFileKey();
            return ObjectIdentifier.builder().key(fileKey).build();
        }).toList();
        Delete delete = Delete.builder().objects(keysToDelete).build();
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket("kizinabucket2")
                .delete(delete)
                .build();
        DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(deleteObjectsRequest);
        if (!deleteObjectsResponse.hasDeleted()) {
            return Collections.emptyList();
        }
        audioLocationRepository.deleteAllById(ids);
        return ids;
    }

    public void moveFileToNecessaryState(String storageType, Long resourceId) {
        Storage storageByType = storageService.getStorageByType(storageType);
        String newBucket = storageByType.getBucket();
        byte[] fileBytes = getFileById(resourceId);
        AudioLocation prevAudioLocation = findAudioLocation(resourceId);
        String fileName = prevAudioLocation.getFileKey();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(newBucket)
                .key(fileName)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(newBucket)
                .key(fileName)
                .build();
        HeadObjectResponse response = s3Client.headObject(headObjectRequest);
        if (response == null) {
            throw new CannotUploadFileToBucket();
        }
        prevAudioLocation.setBucketName(newBucket);
        AudioLocation savedAudioLocation = saveAudioLocation(prevAudioLocation);
        log.info("Audio file was relocated to " + newBucket + " s3-bucket. Updated audio location: " + savedAudioLocation);
    }
}
