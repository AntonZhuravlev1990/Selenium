package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardOrderTest {  private ChromeDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless"); // для CI
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Позитивный тест: успешная отправка формы с валидными данными")
    void shouldSuccessfullySubmitFormWithValidData() {
        // Открываем страницу
        driver.get("http://localhost:9999");

        // Находим поля и заполняем их
        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+79261234567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        // Находим кнопку и нажимаем
        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        // Ждем появления сообщения об успехе
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=order-success]")));

        // Проверяем текст сообщения
        WebElement successMessage = driver.findElement(By.cssSelector("[data-test-id=order-success]"));
        assertTrue(successMessage.isDisplayed());
        assertEquals("Ваша заявка успешно отправлена!", successMessage.getText().trim());
    }

    @Test
    @DisplayName("Негативный тест: пустая форма отправки")
    void shouldShowErrorForEmptyForm() {
        driver.get("http://localhost:9999");

        // Нажимаем кнопку без заполнения полей
        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        // Проверяем появление ошибки валидации для имени
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=name] .input__sub")));
        WebElement nameError = driver.findElement(By.cssSelector("[data-test-id=name] .input__sub"));
        assertTrue(nameError.isDisplayed());
        assertEquals("Поле обязательно для заполнения", nameError.getText());

        // Проверяем ошибку для телефона
        WebElement phoneError = driver.findElement(By.cssSelector("[data-test-id=phone] .input__sub"));
        assertTrue(phoneError.isDisplayed());
        assertEquals("Поле обязательно для заполнения", phoneError.getText());
    }

    @Test
    @DisplayName("Негативный тест: имя с латиницей")
    void shouldShowErrorForLatinName() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Ivan Ivanov");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+79261234567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=name] .input__sub")));
        WebElement nameError = driver.findElement(By.cssSelector("[data-test-id=name] .input__sub"));
        assertTrue(nameError.isDisplayed());
        assertEquals("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы.", nameError.getText());
    }

    @Test
    @DisplayName("Негативный тест: имя с цифрами")
    void shouldShowErrorForNameWithDigits() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов123");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+79261234567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=name] .input__sub")));
        WebElement nameError = driver.findElement(By.cssSelector("[data-test-id=name] .input__sub"));
        assertTrue(nameError.isDisplayed());
        assertEquals("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы.", nameError.getText());
    }

    @Test
    @DisplayName("Негативный тест: имя с дефисом (допустимый символ)")
    void shouldAcceptNameWithHyphen() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Салтыков-Щедрин Михаил");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+79261234567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=order-success]")));
        WebElement successMessage = driver.findElement(By.cssSelector("[data-test-id=order-success]"));
        assertTrue(successMessage.isDisplayed());
        assertEquals("Ваша заявка успешно отправлена!", successMessage.getText().trim());
    }

    @Test
    @DisplayName("Негативный тест: телефон без +")
    void shouldShowErrorForPhoneWithoutPlus() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("79261234567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=phone] .input__sub")));
        WebElement phoneError = driver.findElement(By.cssSelector("[data-test-id=phone] .input__sub"));
        assertTrue(phoneError.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", phoneError.getText());
    }

    @Test
    @DisplayName("Негативный тест: телефон с недостаточным количеством цифр")
    void shouldShowErrorForPhoneWithFewDigits() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+7926123456");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=phone] .input__sub")));
        WebElement phoneError = driver.findElement(By.cssSelector("[data-test-id=phone] .input__sub"));
        assertTrue(phoneError.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", phoneError.getText());
    }

    @Test
    @DisplayName("Негативный тест: телефон с буквами")
    void shouldShowErrorForPhoneWithLetters() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+7926ABC4567");

        WebElement checkbox = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__box"));
        checkbox.click();

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=phone] .input__sub")));
        WebElement phoneError = driver.findElement(By.cssSelector("[data-test-id=phone] .input__sub"));
        assertTrue(phoneError.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", phoneError.getText());
    }

    @Test
    @DisplayName("Негативный тест: невыставленный чекбокс согласия")
    void shouldShowErrorWhenCheckboxNotChecked() {
        driver.get("http://localhost:9999");

        WebElement nameField = driver.findElement(By.cssSelector("[data-test-id=name] input"));
        nameField.sendKeys("Иванов Иван");

        WebElement phoneField = driver.findElement(By.cssSelector("[data-test-id=phone] input"));
        phoneField.sendKeys("+79261234567");

        // НЕ ставим чекбокс

        WebElement button = driver.findElement(By.cssSelector("[data-test-id=action] button"));
        button.click();

        // Проверяем, что чекбокс подсвечен как ошибочный
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=agreement].input_invalid")));
        WebElement agreement = driver.findElement(By.cssSelector("[data-test-id=agreement]"));
        assertTrue(agreement.getAttribute("class").contains("input_invalid"));
    }
}
}
