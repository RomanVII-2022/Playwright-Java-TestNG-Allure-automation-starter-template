# Playwright Java Test Framework Template

A production-ready bootstrap template for web automation testing built with [Playwright](https://playwright.dev/), [TestNG](https://testng.org/), and [Allure](https://allure.qatag.io/) reporting. This framework follows industry-standard design patterns including the Page Object Model (POM) and business layer abstraction, making it easy to extend and maintain for real-world testing needs.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Allure Reporting](#allure-reporting)
- [Customization Guide](#customization-guide)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before getting started, ensure your system has the following installed:

| Requirement | Minimum Version | Notes |
|---|---|---|
| Java Development Kit | 17+ | JDK 25 is recommended |
| Apache Maven | 3.8+ | For dependency management and test execution |
| Git | Latest | For version control |

Verify your installations:

```bash
java -version
mvn -version
```

---

## Quick Start

Follow these steps to get the framework up and running in under five minutes:

1. **Clone or download** this template repository to your local machine.

2. **Configure your environment.** Copy the example environment file and update it with your credentials:

   ```bash
   cp .env.example .env
   ```

   Edit `.env` and fill in your actual email addresses and passwords.

3. **Install Java dependencies:**

   ```bash
   mvn clean install
   ```

4. **Install Playwright browsers.** The framework uses Playwright under the hood, so you need to download the browser binaries:

   ```bash
   npx playwright install chromium
   ```

   To install all three browser engines (Chromium, Firefox, and WebKit):

   ```bash
   npx playwright install
   ```

5. **Run your first test:**

   ```bash
   mvn test
   ```

If everything is configured correctly, you should see output indicating that tests passed successfully.

---

## Project Structure

The project follows a standard Maven layout with a layered architecture:

```
playwright-java/
├── pom.xml                          # Maven build configuration
├── testng.xml                       # TestNG suite definition
├── .env.example                     # Environment variable template
├── .gitignore                       # Git ignore rules
│
├── src/
│   ├── main/java/org/automation/
│   │   ├── base/
│   │   │   └── BasePage.java        # Shared page interaction utilities
│   │   ├── config/
│   │   │   ├── ConfigManager.java   # Configuration loader & resolver
│   │   │   └── PlatformConfig.java  # Data class for platform settings
│   │   ├── pages/
│   │   │   └── LoginPage.java       # Page Object for login functionality
│   │   └── business/
│   │       └── LoginBusiness.java   # Business logic orchestration layer
│   │
│   ├── main/resources/
│   │   ├── platform_data.json       # Platform/environment configurations
│   │   └── logback.xml              # Logging configuration
│   │
│   └── test/java/org/automation/
│       ├── base/
│       │   └── BaseTest.java        # Test base class (browser lifecycle)
│       └── tests/
│           └── LoginTest.java       # Example test class
│
├── target/                          # Maven build output (auto-generated)
└── logs/                            # Application log files
```

### Directory Breakdown

- **`src/main/java`** — Contains all production code: page objects, business logic, and configuration classes.
- **`src/main/resources`** — Holds configuration files and data files that are packaged with the application.
- **`src/test/java`** — Contains all test classes and the test base class that manages browser lifecycle.
- **`logs/`** — Rolling log files generated during test execution (configured in `logback.xml`).

---

## Architecture Overview

This framework implements a three-layer architecture commonly used in enterprise test automation:

### Layer 1: Test Layer (`src/test/java`)

Test classes contain only assertions and test flow orchestration. They delegate all interaction with the application to the business layer. This keeps tests readable, focused, and independent of implementation details.

Example from `LoginTest.java`:

```java
@Test
public void verifyLoginPageLoadsTest() {
    loginBusiness.login(platformConfig.getUrl(), platformConfig.getEmail(), platformConfig.getPassword());
    
    String title = getPage().title();
    assertNotNull("Page title should not be null", title);
}
```

### Layer 2: Business Layer (`src/main/java/org/automation/business`)

The business layer orchestrates page objects to perform higher-level operations. It encapsulates the sequence of steps needed to complete a business task, such as logging in, navigating to a dashboard, or submitting a form. This layer is reusable across multiple tests.

Example from `LoginBusiness.java`:

```java
public void login(String url, String email, String password) {
    loginPage.navigateToLoginPage(url);
    loginPage.waitForPageLoad();
    loginPage.login(email, password);
}
```

### Layer 3: Page Object Layer (`src/main/java/org/automation/pages`)

Page Object classes represent individual pages or components of the application under test. Each class encapsulates:

- **Locators** — CSS selectors, XPath expressions, or Playwright locators for elements on the page.
- **Actions** — Methods that interact with elements (click, fill, select, etc.).
- **Queries** — Methods that read element state (text content, visibility, attributes).
- **Validations** — Methods that verify expected conditions (wait for success message, validate logo presence).

This separation ensures that when the UI changes, you only need to update the page object rather than every test that uses it.

### Base Classes

- **`BaseTest`** (`src/test/java`) — Manages the browser lifecycle: launching the browser, creating browser contexts, opening pages, and tearing down resources after each test. It also captures screenshots on test failures for Allure reports.
- **`BasePage`** (`src/main/java`) — Provides a shared pool of common web interaction methods (navigation, clicking, filling forms, waiting, screenshots) that all page objects inherit.

---

## Configuration

### Platform Configuration

The `platform_data.json` file defines test environments, credentials, and browser settings. Each entry represents a "platform" — for example, a web application or an API endpoint.

```json
[
  {
    "name": "web",
    "url": "https://thinking-tester-contact-list.herokuapp.com",
    "email": "test.user@test.com",
    "password": "12345678",
    "browser": "chromium",
    "headless": false
  }
]
```

You can add as many platform entries as you need. The framework will match the platform name passed at runtime (via `-Dplatform=...`) to the correct configuration.

### Environment Variable Substitution

The framework supports environment variable substitution in `platform_data.json`. Use the `${ENV_VAR_NAME}` syntax to reference environment variables:

```json
{
  "email": "${WEB_EMAIL}",
  "password": "${WEB_PASSWORD}"
}
```

When the configuration is loaded, `${WEB_EMAIL}` will be replaced with the value of the `WEB_EMAIL` environment variable. If the variable is not set, it will be replaced with an empty string and a warning will be logged.

### Logging Configuration

Logging is configured via `logback.xml` and writes to both the console and a rolling log file:

- **Console output** — Immediate visibility during test execution.
- **File output** — Stored in `logs/automation.log`, with daily rotation and a 30-day retention policy.
- **Log levels** — Application logs run at `DEBUG` level; Playwright internal logs are suppressed at `WARN` level to reduce noise.

---

## Running Tests

### Default Execution

Run all tests with default settings (Chromium, web platform, non-headless):

```bash
mvn test
```

### Browser Selection

Override the browser engine using the `browser` system property:

```bash
mvn test -Dbrowser=chromium    # Default
mvn test -Dbrowser=firefox     # Firefox
mvn test -Dbrowser=webkit      # WebKit (Safari engine)
```

### Platform Selection

Target a specific platform defined in `platform_data.json`:

```bash
mvn test -Dplatform=web    # Web platform
mvn test -Dplatform=api    # API platform
```

### Headless Mode

Run tests without a visible browser window (useful for CI/CD pipelines):

```bash
mvn test -Dheadless=true
```

### Slow Motion

Add a delay between actions for debugging. The value is in milliseconds:

```bash
mvn test -DslowMo=100
```

### Running a Single Test

Execute a specific test method using TestNG's filter:

```bash
mvn test -Dtest=LoginTest#verifyLoginPageLoadsTest
```

### Combined Parameters

You can combine multiple parameters in a single command:

```bash
mvn test -Dbrowser=firefox -Dplatform=web -Dheadless=true -DslowMo=50
```

### Parallel Execution

The `testng.xml` is configured for parallel test execution (`parallel="tests"`). Each `<test>` block in the suite runs in its own thread. Adjust the `thread-count` attribute to control concurrency.

---

## Allure Reporting

Allure generates rich, interactive test reports with step-by-step execution details, screenshots on failure, and environment information.

### Generate and View a Report

After running tests, the results are written to the `allure-results/` directory. To generate a report:

```bash
mvn clean test
allure serve allure-results
```

The `allure serve` command starts a local web server and opens the report in your default browser.

### Combined Command

Run tests and generate the report in one step:

```bash
mvn clean test allure:serve
```

### What the Report Includes

- **Test status** — Passed, failed, broken, or skipped with full stack traces.
- **Steps** — Each annotated step in the test appears as a collapsible section.
- **Attachments** — Screenshots captured automatically on test failures.
- **Labels and categories** — Tests are tagged with epic, feature, severity, and other metadata for filtering.
- **Environment data** — Browser, platform, and headless mode used for each run.

---

## Customization Guide

### Step 1: Update Project Metadata

Open `pom.xml` and update the following fields to match your organization:

- `groupId` — Your organization's reverse domain name (e.g., `com.acme`).
- `artifactId` — A descriptive project identifier.
- `description` — A brief description of the project.

### Step 2: Add Your Platforms

Edit `src/main/resources/platform_data.json` and add entries for each environment you need to test. Each entry should include:

- `name` — A unique identifier used at runtime (`-Dplatform=<name>`).
- `url` — The base URL of the application.
- `email` and `password` — Test credentials.
- `browser` — Default browser for this platform.
- `headless` — Whether to run in headless mode by default.

### Step 3: Create Page Objects

For each page or component in your application, create a new class in `src/main/java/org/automation/pages/`:

```java
public class DashboardPage extends BasePage {
    private static final String WELCOME_MESSAGE = "#welcome";

    public DashboardPage(Page page) {
        super(page);
    }

    public String getWelcomeMessage() {
        return page.locator(WELCOME_MESSAGE).textContent();
    }

    public void waitForDashboardLoaded() {
        waitForElementVisible(WELCOME_MESSAGE);
    }
}
```

### Step 4: Create Business Layer Classes

Orchestrate page objects in `src/main/java/org/automation/business/` to represent end-to-end workflows:

```java
public class DashboardBusiness {
    private final DashboardPage dashboardPage;

    public DashboardBusiness(Page page) {
        this.dashboardPage = new DashboardPage(page);
    }

    public String navigateToDashboard(String url) {
        dashboardPage.navigateTo(url);
        dashboardPage.waitForDashboardLoaded();
        return dashboardPage.getWelcomeMessage();
    }
}
```

### Step 5: Write Tests

Create test classes in `src/test/java/org/automation/tests/`. Keep them focused on assertions and test flow:

```java
@Test
public void verifyDashboardLoadsAfterLogin() {
    loginBusiness.login(platformConfig.getUrl(), platformConfig.getEmail(), platformConfig.getPassword());
    String message = dashboardBusiness.navigateToDashboard(platformConfig.getUrl());
    assertNotNull(message);
}
```

### Step 6: Register Tests in testng.xml

Add your new test classes to `testng.xml` under the appropriate `<test>` block:

```xml
<classes>
    <class name="org.automation.tests.LoginTest"/>
    <class name="org.automation.tests.DashboardTest"/>
</classes>
```

### Step 7: Configure Environment Variables

Update `.env` with your actual credentials and environment-specific values.

---

## Troubleshooting

### Browser Launch Failures

If you see errors like `Executable doesn't exist` or `BrowserType.launch: Executable doesn't exist`, Playwright browsers are not installed. Run:

```bash
npx playwright install chromium
```

To install all browser engines:

```bash
npx playwright install
```

### "Platform not found" Error

This error occurs when the platform name passed at runtime does not match any entry in `platform_data.json`. Verify that:

1. The platform name in `platform_data.json` is spelled correctly.
2. The `-Dplatform=` value matches exactly (case-sensitive).

### Environment Variables Not Substituted

If `${ENV_VAR_NAME}` placeholders in `platform_data.json` are not being replaced:

1. Ensure the environment variable is actually set in your shell:

   ```bash
   echo $WEB_EMAIL
   ```

2. If using an IDE, check that environment variables are configured in the run configuration.

3. Check the logs for warnings about missing environment variables.

### Allure Report Not Generating

If `allure serve` fails:

1. Ensure Allure Command Line is installed:

   ```bash
   npm install -g allure-commandline
   ```

2. Verify that the `allure-results/` directory contains JSON files after running tests.

3. Check that the `allure-maven` plugin is present in `pom.xml`.

### IntelliJ "Java File Outside Source Root" Warning

If IntelliJ shows this warning for your Java files:

1. Right-click `src/main/java` → **Mark Directory as** → **Sources Root**.
2. Right-click `src/test/java` → **Mark Directory as** → **Test Sources Root**.

Or re-import the project as a Maven project: right-click `pom.xml` → **Maven** → **Reload Project**.

### Tests Fail on CI/CD

For headless CI environments:

1. Always run with `-Dheadless=true`.
2. Install Playwright system dependencies:

   ```bash
   npx playwright install-deps
   ```

3. Ensure the browser binaries are cached or pre-installed in your CI pipeline.

### Port or Permission Errors

If you see sandbox or permission errors when launching Chromium:

1. The framework already includes `--no-sandbox` and `--disable-setuid-sandbox` in launch arguments.
2. If running as root in Docker, ensure you have the necessary capabilities.
3. Try clearing the Playwright cache:

   ```bash
   npx playwright install --force
   ```

---

## License

This template is provided as-is for testing and automation purposes.
