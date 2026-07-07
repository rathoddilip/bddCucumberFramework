# ============================================================
# Feature: Fixerra Login Page (Mobile OTP Flow)
# The login page accepts a 10-digit Aadhaar-linked mobile number.
# Authentication is OTP-based — there is no password field.
# ============================================================

Feature: Login Page

  Background:
    Given the user is on the login page

  # ----------------------------------------------------------
  # Page Load Verification
  # ----------------------------------------------------------

  @smoke @happy-path
  Scenario: Login page loads with all required elements
    Then the login page heading should be displayed
    And the Get Started button should be present

  # ----------------------------------------------------------
  # Happy Path
  # ----------------------------------------------------------

  @smoke @happy-path
  Scenario: Get Started button is enabled after entering a valid mobile number
    When the user enters mobile number "9876543210"
    Then the Get Started button should be enabled

  # ----------------------------------------------------------
  # Empty Field Validation
  # ----------------------------------------------------------

  @regression @validation
  Scenario: Validation error shown when Get Started is clicked without mobile
    When the user clicks Get Started without entering a mobile number
    Then a validation error should be displayed

  # ----------------------------------------------------------
  # Mobile Number Format Validations
  # ----------------------------------------------------------

  @regression @validation
  Scenario: Validation error for mobile number shorter than 10 digits
    When the user enters mobile number "98765"
    And the user clicks Get Started
    Then a validation error should be displayed

  @regression @validation
  Scenario: Validation error for mobile number longer than 10 digits
    When the user enters mobile number "12345678901"
    And the user clicks Get Started
    Then a validation error should be displayed

  @regression @validation
  Scenario: Validation error for alphabetic characters in mobile field
    When the user enters mobile number "abcdefghij"
    And the user clicks Get Started
    Then a validation error should be displayed

  @regression @validation
  Scenario: Validation error for special characters in mobile field
    When the user enters mobile number "@#$%^&*()"
    And the user clicks Get Started
    Then a validation error should be displayed

  # ----------------------------------------------------------
  # Data-Driven — Multiple Invalid Mobile Formats
  # ----------------------------------------------------------

  @regression @data-driven
  Scenario Outline: Validation triggered for various invalid mobile inputs
    When the user enters mobile number "<mobile>"
    And the user clicks Get Started
    Then a validation error should be displayed

    Examples:
      | mobile      |
      | 00000       |
      | 1234abc567  |
      | 0000000000  |
