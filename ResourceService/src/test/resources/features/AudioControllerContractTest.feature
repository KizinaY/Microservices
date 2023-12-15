Feature: AudioController contract tests

  Scenario: Upload a valid audio file
    Given a valid MP3 file
    When the client calls to POST /resources
    Then the client receives status code of 200
    And the client receives response parameter ID

  Scenario: Upload an invalid audio file
    Given an invalid MP3 file
    When the client calls to POST /resources
    Then the client receives status code of 400