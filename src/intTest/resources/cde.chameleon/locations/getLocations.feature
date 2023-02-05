Feature: Get all locations

  Background:
    Given I have added a test location

  Scenario: Successfully getting locations with read role
    Given I am logged in as user with role to read locations
    When I get the locations
    Then the list of locations contain the added location

  Scenario: Failure to get location as a guest
    Given I am logged in as a guest
    When I get the locations
    Then the access is forbidden