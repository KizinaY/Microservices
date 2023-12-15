package com.kizina.componenttest;

import com.kizina.componenttest.util.AudioLocation;
import com.kizina.componenttest.util.Metadata;
import com.kizina.componenttest.util.TestAudioLocationRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("component")
public class StepDefinitionsTest {

    @Value("${bucket.name}")
    private String S3_BUCKET_NAME;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TestAudioLocationRepository testAudioLocationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Resource multipartFileResource;
    private ResponseEntity<Map> response;

    @Given("a valid MP3 file")
    public void aValidMP3File() throws IOException {
        byte[] audioData = readResourceAsBytes("/audio/sample-3s.mp3");
        multipartFileResource = new ByteArrayResource(audioData) {
            @Override
            public String getFilename() {
                return "sample-3s.mp3";
            }
        };
    }

    @When("^the client calls to POST /resources")
    public void theClientCallsToPOSTResources() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        response = restTemplate.exchange("http://localhost:8030/resources", HttpMethod.POST, requestEntity, Map.class);
    }

    @Then("the client receives status code of (\\d+)$")
    public void theClientReceivesStatusCodeOf(int statusCode) {
        assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("the client receives response parameter ID")
    public void theClientReceivesResponseParameterID() {
        assertNotNull(response.getBody().get("id"));
    }

    @Then("the file is in S3 bucket")
    public void theFileInS3Bucket() {
        assertNotNull(getFileById(Long.valueOf((Integer) response.getBody().get("id"))));
    }

    @Then("the resourceId is in rabbitMQ")
    public void theResourceIdIsInRabbitMQ() {
        Object received = rabbitTemplate.receiveAndConvert("audio-queue", 1000);
        assertNotNull(received);
    }

    @Then("SongService receives Metadata")
    public void songServiceReceivesMetadata() {

        String url = "http://localhost:8033/songs/" + response.getBody().get("id");
        Awaitility.await()
                .atMost(Duration.ONE_MINUTE)
                .until(() ->
                        {
                            try {
                                ResponseEntity<Metadata> responseEntity = restTemplate.getForEntity(url, Metadata.class);
                                HttpStatusCode status = responseEntity.getStatusCode();
                                return HttpStatus.OK.equals(status) && responseEntity.getBody() != null;
                            } catch (HttpClientErrorException e) {
                                return false;
                            }
                        }
                );
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

    private byte[] getFileById(Long id) {
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
        Optional<AudioLocation> audioLocation = testAudioLocationRepository.findById(id);
        if (audioLocation.isPresent()) {
            return audioLocation.get();
        } else {
            throw new RuntimeException("NoSuchAudioException id: " + id);
        }
    }

}
