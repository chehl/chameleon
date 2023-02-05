Feature: Delete a location

  Background:
    Given I have added a test location

  Scenario: Successfully deleting a location with write role
    Given I am logged in as user with role to write locations
    When I delete the location
    Then the location has been deleted successfully

  Scenario: Failure to delete location with read role
    Given I am logged in as user with role to read locations
    When I delete the location
    Then the access is forbidden

  Scenario: Failure to delete location as a guest
    Given I am logged in as a guest
    When I delete the location
    Then the access is forbidden

  Scenario: Failure to delete location by a non-existing id
    Given I am logged in as user with role to write locations
    When I delete a location by a non-existing id
    Then it is not found