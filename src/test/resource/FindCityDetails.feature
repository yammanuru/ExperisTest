Feature: Testing a City Bikes REST API
  As a user I want to verify that the a given city is in corresponding country and that we can return
  the city’s corresponding latitude and longitude.

  Scenario: Successfully find the latitude and longitude for a city
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of "Frankfurt"
    Then Receive a positive response
    And response contains country equals "Germany"
    And response contains latitude and longitude

  Scenario: Successfully find the latitude and longitude for a city
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of "Moscow"
    Then Receive a positive response
    And response contains country equals "Russia"
    And response contains latitude and longitude

  Scenario: Must receive a negative response for a city not in the network
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of "London"
    Then Receive a negative response

  Scenario: Must receive a negative response for a city with malformed characters
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of "Fr@nkfurt"
    Then Receive a negative response

  Scenario: Must receive a negative response for a city with malformed characters
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of "Frankfurt123!"
    Then Receive a negative response

  Scenario: Must receive a negative response when provided empty city value
    Given City bikes API is up and User has required privilege to access
    When A user requested details for city of ""
    Then Receive a negative response

  Scenario: Successfully find a city exists in the network
    Given City bikes API is up and User has required privilege to access
    When A user requested details for all cities
    Then Receive a positive response
    And verify "Frankfurt" exists in the cities

  Scenario: Successfully find total number of free bikes available in the network
    Given City bikes API is up and User has required privilege to access
    When A user requested details for all cities
    Then Receive a positive response
    And get total number of free bikes in the network

  Scenario: Successfully find total number of electric bikes available in the network
    Given City bikes API is up and User has required privilege to access
    When A user requested details for all cities
    Then Receive a positive response
    And get total number of electric bikes in the network
