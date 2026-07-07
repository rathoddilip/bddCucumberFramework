# Fixerra Login BDD Framework

A **Behavior Driven Development (BDD)** UI automation framework built with:

| Tool | Purpose |
|---|---|
| Java 17 | Language |
| Maven | Build & dependency management |
| Selenium WebDriver 4.x | Browser automation |
| Cucumber 7 | BDD engine (Gherkin) |
| JUnit 5 Platform | Test runner |
| WebDriverManager | Auto browser driver management |
| Log4j2 | Logging |

---

## Project Structure

```
login-bdd-framework/
├── src/
│   ├── main/java/com/fixerra/bdd/
│   │   ├── config/ConfigReader.java      # reads config.properties
│   │   ├── driver/DriverManager.java     # thread-safe WebDriver lifecycle
│   │   ├── pages/
│   │   │   ├── BasePage.java             # reusable wait & action helpers
│   │   │   └── LoginPage.java            # login page element locators & actions
│   │   └── utils/ScreenshotUtil.java     # screenshot capture utility
│   └── test/
│       ├── java/com/fixerra/bdd/
│       │   ├── hooks/Hooks.java          # @Before / @After Cucumber hooks
│       │   ├── runner/TestRunner.java    # JUnit Platform Suite entry point
│       │   └── steps/LoginSteps.java    # Gherkin step definitions
│       └── resources/
│           ├── features/login.feature   # all Gherkin scenarios
│           ├── config.properties        # runtime configuration
│           └── log4j2.xml               # logging configuration
├── reports/                             # generated at runtime
├── screenshots/                         # failure screenshots
└── pom.xml
```

---

## Prerequisites

- Java 17 or higher (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Google Chrome installed (default browser)

---

## Configuration

`config.properties` is gitignored and must be created locally. Copy the example file and set your values:

```bash
cp src/test/resources/config.properties.example src/test/resources/config.properties
```

Then edit `src/test/resources/config.properties`:

```properties
browser=chrome          # chrome | firefox | edge
headless=false          # true for CI / pipeline runs
base.url=https://your-app-url/login
implicit.wait=10
explicit.wait=15
page.load.timeout=30
screenshot.dir=screenshots
```

All values can be overridden at runtime via system properties (see examples below).

---

## Running Tests

### Run all scenarios
```bash
mvn test
```

### Run only smoke tests
```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### Run only regression tests
```bash
mvn test -Dcucumber.filter.tags="@regression"
```

### Run with Firefox
```bash
mvn test -Dbrowser=firefox
```

### Run in headless mode (for CI)
```bash
mvn test -Dheadless=true
```

### Combined: headless Chrome, smoke tag only
```bash
mvn test -Dheadless=true -Dcucumber.filter.tags="@smoke"
```

---

## Reports

After `mvn test`, the following reports are generated:

| Report | Location |
|---|---|
| Cucumber HTML | `reports/cucumber-report.html` |
| Cucumber JSON | `reports/cucumber.json` |
| Timeline | `reports/timeline/index.html` |
| Failure screenshots | `screenshots/` |
| Log file | `logs/test-run.log` |

Open the HTML report in any browser:
```bash
# Windows
start reports\cucumber-report.html

# macOS / Linux
open reports/cucumber-report.html
```

---

## Gherkin Tags

| Tag | Description |
|---|---|
| `@smoke` | Core happy-path scenario — run before every deployment |
| `@regression` | Full regression suite |
| `@happy-path` | Positive / success scenarios |
| `@negative` | Negative / failure scenarios |
| `@validation` | Field validation scenarios |
| `@data-driven` | Scenario Outline with Examples table |
| `@wip` | Work-in-progress — skipped by default |

---

## Updating Locators

All login page locators are in `LoginPage.java`.  
Update the `@FindBy` annotations to match your application's actual DOM selectors:

```java
@FindBy(css = "input[name='mobile']")
private WebElement mobileNumberField;

@FindBy(css = "input[name='password']")
private WebElement passwordField;

@FindBy(css = "button[type='submit']")
private WebElement loginButton;
```

---

## Adding More Features

1. Create a new `.feature` file under `src/test/resources/features/`
2. Create a matching `*Steps.java` file under `src/test/java/com/fixerra/bdd/steps/`
3. Create a `*Page.java` under `src/main/java/com/fixerra/bdd/pages/`
4. Run `mvn test` — Cucumber auto-discovers all feature files and step definitions.
