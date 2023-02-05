Feature: Add new location

  Scenario: Successfully adding a new location with write role
    Given I am logged in as user with role to write locations
    When I add a location
    Then the location has been added successfully

  Scenario: Failure to add location with read role
    Given I am logged in as user with role to read locations
    When I add a location
    Then the access is forbidden

  Scenario: Failure to add location as a guest
    Given I am logged in as a guest
    When I add a location
    Then the access is forbidden

  Scenario: Failure to add a second location with the same name
    Given I have added a test location
    And I am logged in as user with role to write locations
    When I add a second location with the same name as the test location
    Then it is unprocessable
