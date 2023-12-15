package com.kizina.resourceservice.controller;

import com.kizina.resourceservice.producer.RabbitMQProducer;
import com.kizina.resourceservice.service.AudioService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefinitionsForTest {

    private MultipartFile mockMultipartFile;

    @Autowired
    private AudioService mockAudioService;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private ResponseEntity<Map> response;

    @Given("a valid MP3 file")
    public void aValidMP3File() throws IOException {
        mockMultipartFile = mock(MultipartFile.class);
        byte[] mp3Content = "It is a test mock MP3 content".getBytes();
        when(mockMultipartFile.getBytes()).thenReturn(mp3Content);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("mockMp3.mp3");
        when(mockMultipartFile.getContentType()).thenReturn("audio/mpeg");
        doReturn(8L).when(mockAudioService).uploadFile(any(MultipartFile.class), anyString());
        doNothing().when(rabbitMQProducer).sendMessage(any(String.class));
    }


    @Given("an invalid MP3 file")
    public void anInvalidMP3File() throws IOException {
        mockMultipartFile = mock(MultipartFile.class);
        byte[] invalidContent = "Invalid content".getBytes();
        when(mockMultipartFile.getBytes()).thenReturn(invalidContent);
        when(mockMultipartFile.getContentType()).thenReturn("invalidContentType");
    }

    @When("^the client calls to POST /resources")
    public void theClientCallsToPOSTResources() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        Resource multipartFileResource = new ByteArrayResource(mockMultipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return mockMultipartFile.getOriginalFilename();
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", multipartFileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        response = testRestTemplate.exchange("/resources", HttpMethod.POST, requestEntity, Map.class);
    }

    @Then("the client receives status code of (\\d+)$")
    public void theClientReceivesStatusCodeOf(int statusCode) {
        assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("the client receives response parameter ID")
    public void theClientReceivesResponseParameterID() {
        assertNotNull(response.getBody().get("id"));
    }

}
