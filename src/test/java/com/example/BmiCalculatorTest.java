package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BmiCalculatorTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Enable Headless mode (required for GitHub Actions and Headless testing)
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (isHeadless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testBmiCalculatorWithMetricInput() {
        // =========================
        // Arrange
        // =========================
        int age = 23;
        String gender = "m";
        int heightCm = 183;
        int weightKg = 103;

        driver.get("https://www.calculator.net/bmi-calculator.html?ctype=metric");

        // =========================
        // Input Age
        // =========================
        WebElement ageInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("cage"))
        );
        ageInput.clear();
        ageInput.sendKeys(String.valueOf(age));

        // =========================
        // Select Gender
        // =========================
        By genderLocator = By.cssSelector("input[name='csex'][value='" + gender + "']");
        WebElement genderRadio = wait.until(
                ExpectedConditions.presenceOfElementLocated(genderLocator)
        );

        if (!genderRadio.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", genderRadio);
        }

        assertTrue(genderRadio.isSelected(), "ไม่สามารถเลือก Male ได้");

        // =========================
        // Input Height
        // =========================
        WebElement heightInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("cheightmeter"))
        );
        heightInput.clear();
        heightInput.sendKeys(String.valueOf(heightCm));

        // =========================
        // Input Weight
        // =========================
        WebElement weightInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("ckg"))
        );
        weightInput.clear();
        weightInput.sendKeys(String.valueOf(weightKg));

        // =========================
        // Click Calculate
        // =========================
        WebElement calculateButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='submit'][value='Calculate']"))
        );
        calculateButton.click();

        // =========================
        // Expected BMI
        // =========================
        double heightMeter = heightCm / 100.0;
        double expectedBmi = weightKg / (heightMeter * heightMeter);
        String expectedBmiText = String.format(Locale.US, "%.1f", expectedBmi);

        // =========================
        // Wait Result
        // =========================
        By bodyLocator = By.tagName("body");
        wait.until(
                ExpectedConditions.textToBePresentInElementLocated(bodyLocator, "BMI = " + expectedBmiText)
        );

        String pageText = driver.findElement(bodyLocator).getText();

        // =========================
        // Verify BMI
        // =========================
        assertTrue(
                pageText.contains("BMI = " + expectedBmiText),
                "BMI ไม่ถูกต้อง"
        );

        // =========================
        // Verify Category
        // =========================
        String expectedCategory;
        if (expectedBmi < 16.0) {
            expectedCategory = "Severe Thinness";
        } else if (expectedBmi < 17.0) {
            expectedCategory = "Moderate Thinness";
        } else if (expectedBmi < 18.5) {
            expectedCategory = "Mild Thinness";
        } else if (expectedBmi < 25.0) {
            expectedCategory = "Normal";
        } else if (expectedBmi < 30.0) {
            expectedCategory = "Overweight";
        } else if (expectedBmi < 35.0) {
            expectedCategory = "Obese Class I";
        } else if (expectedBmi < 40.0) {
            expectedCategory = "Obese Class II";
        } else {
            expectedCategory = "Obese Class III";
        }

        assertTrue(
                pageText.contains("(" + expectedCategory + ")"),
                "Category ไม่ใช่ " + expectedCategory
        );

        System.out.println("--------------------------------");
        System.out.println("Browser : Chrome (Headless)");
        System.out.println("Age     : " + age);
        System.out.println("Height  : " + heightCm);
        System.out.println("Weight  : " + weightKg);
        System.out.println("BMI     : " + expectedBmiText);
        System.out.println("Status  : TEST PASSED");
        System.out.println("--------------------------------");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
