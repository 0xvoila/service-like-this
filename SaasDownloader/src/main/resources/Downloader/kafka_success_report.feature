Feature: Validate the kafka success report
  Validate if the kafka success report provides the correct number or not

  Scenario Outline: Metric measurement of success and failure rate
    Given <no_of_success_requests_sent> requests are 200Ok request
    Given <no_of_failed_requests_sent> requests are 400 request
    Given Key "<resource_key>" for which we want to send requests
    Given Having rate limit of <limit> per minute
    When Added into the kafka "<topic_name>" topic
    When wait for <wait_minutes> minutes to download execute the request
    Then Report should show <no_of_success_requests_sent> plus <no_of_failed_requests_sent> total received request and <no_of_successful_request> successful requests
    Then Report should show <no_of_success_requests_sent> plus <no_of_failed_requests_sent> total received request and <no_of_failure_requests> have failed
    Examples:
      | no_of_success_requests_sent | no_of_successful_request | topic_name       | wait_minutes | limit | resource_key   | no_of_failure_requests | no_of_failed_requests_sent |
      | 5                           | 5                        | downloader-input | 1            | 100   | 201/okta/users | 5                      | 5                          |
      | 20                          | 20                       | downloader-input | 1            | 100   | 202/okta/users | 20                     | 20                         |
      | 10                          | 10                       | downloader-input | 1            | 100   | 203/okta/users | 10                     | 10                         |



