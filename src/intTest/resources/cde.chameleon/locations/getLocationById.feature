Feature: Get location by id

  Background:
    Given I have added a test location

  Scenario: Successfully getting a location by id with read role
    Given I am logged in as user with role to read locations
    When I get the location by id
    Then the location with the provided id is returned

  Scenario: Failure to get location by id as a guest
    Given I am logged in as a guest
    When I get the location by id
    Then the access is forbidden

  Scenario: Failure to get location by a non-existing id
    Given I am logged in as user with role to read locations
    When I get a location by a non-existing id
    Then it is not found
