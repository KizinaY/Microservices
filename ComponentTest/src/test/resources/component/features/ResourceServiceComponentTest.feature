Feature: ResourceService component tests

  Scenario: Upload a valid audio file
    Given a valid MP3 file
    When the client calls to POST /resources
    Then the client receives status code of 200
    And the client receives response parameter ID
    And the file is in S3 bucket
    And the resourceId is in rabbitMQ