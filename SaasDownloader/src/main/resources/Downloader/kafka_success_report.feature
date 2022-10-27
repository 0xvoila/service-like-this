Feature: Validate the kafka success report
  Validate if the kafka success report provides the correct number or not

  Scenario Outline: All requests are success
    Given <no_of_requests> successful requests
    When Added into the kafka "<topic_name>" topic
    When wait for <wait_minutes> minutes to download execute the request
    Then Report should show <no_of_requests> total received request and <no_of_successful_request> successful requests

    Examples:
      | no_of_requests | no_of_successful_request | topic_name       | wait_minutes |
      | 50             | 50                       | downloader-input | 2            |


