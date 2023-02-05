Feature: Update an existing location

  Background:
    Given I have added a test location

  Scenario: Successfully updating a location with write role
    Given I am logged in as user with role to write locations
    When I update the location
    Then the location has been updated successfully

  Scenario: Failure to update a location with read role
    Given I am logged in as user with role to read locations
    When I update the location
    Then the access is forbidden

  Scenario: Failure to update a location as a guest
    Given I am logged in as a guest
    When I update the location
    Then the access is forbidden

  Scenario: Failure to update name of location to existing one
    Given I have added a test location "other"
    And I am logged in as user with role to write locations
    When I update the name of the location "other" to the same name as the test location
    Then it is unprocessable