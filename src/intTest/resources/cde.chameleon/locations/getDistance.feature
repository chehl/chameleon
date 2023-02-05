Feature: Get distance between two locations

  Background:
    Given I have added test location "Hamburg" with latitude 53.55 and longitude 9.99
    And I have added test location "Munich" with latitude 48.14 and longitude 11.58

  Scenario: Successfully getting the distance between two locations with read role
    Given I am logged in as user with role to read locations
    When I get the distance between locations "Hamburg" and "Munich"
    Then the distance is returned as about 612 kilometers

  Scenario: Failure to get distance between two locations as a guest
    Given I am logged in as a guest
    When I get the distance between locations "Hamburg" and "Munich"
    Then the access is forbidden