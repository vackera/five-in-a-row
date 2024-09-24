package gzs.fiar.selenium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gzs.fiar.dto.PlayerNameDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangePlayerNameTest {

    private final String webServerAddress = "http://localhost:8080";

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private WebDriver driver;
    JavascriptExecutor js;

    @BeforeAll
    static void setUpClass() {

        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
    }

    @AfterEach
    void teardown() {

        driver.quit();
    }

    @Test
    public void changePlayerNameTest_withValidInput() throws InterruptedException {
        driver.get(webServerAddress);

        driver.manage().window().setSize(new Dimension(500, 1000));
        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Test Player");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);

        String expected = (String) js.executeScript("return MESSAGES.PLAYER_NAME_CHANGED;");
        String actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    @Test
    public void changePlayerNameTest_withInvalidInputs() throws InterruptedException {

        driver.get(webServerAddress);

        driver.manage().window().setSize(new Dimension(500, 1000));

        Thread.sleep(300);
        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Test_Player");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);
        Thread.sleep(100);

        String expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");
        String actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        Thread.sleep(300);
        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Test Pl@yer");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);
        Thread.sleep(100);

        expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");
        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        Thread.sleep(300);
        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("  ");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);
        Thread.sleep(100);

        expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");
        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        js.executeScript("document.getElementById('player-name').removeAttribute('maxlength');");

        Thread.sleep(300);
        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Valid Name But Too Long");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);
        Thread.sleep(100);

        expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");
        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    @Test
    public void changePlayerNameTest_withInvalidInputs_disabledJavascriptCheck() throws JsonProcessingException, InterruptedException {

        driver.get(webServerAddress);

        js.executeScript("isPlayerNameValid = function() { return true; }");
        js.executeScript("reloadPage = function() { }");

        driver.manage().window().setSize(new Dimension(500, 1000));

        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Test_Player");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);

        String xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        Map<String, List<String>> fieldErrors = parseFieldErrors(xhrResponseData);

        PlayerNameDto playerNameDto = new PlayerNameDto("Test_Player");
        Set<ConstraintViolation<PlayerNameDto>> violations = validator.validate(playerNameDto);

        List<String> expectedMessages = extractValidationMessages(violations);
        List<String> actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        Thread.sleep(200);

        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("Te");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        playerNameDto = new PlayerNameDto("Te");
        violations = validator.validate(playerNameDto);

        expectedMessages = extractValidationMessages(violations);
        actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        Thread.sleep(200);

        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys("");
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        playerNameDto = new PlayerNameDto("");
        violations = validator.validate(playerNameDto);

        expectedMessages = extractValidationMessages(violations);
        actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    private Map<String, List<String>> parseFieldErrors(String xhrResponseData) {

        Map<String, List<String>> fieldErrorsMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseData = objectMapper.readValue(xhrResponseData, new TypeReference<Map<String, Object>>() {});

            String responseText = (String) responseData.get("responseText");

            JsonNode rootNode = objectMapper.readTree(responseText);
            JsonNode fieldErrorsNode = rootNode.get("fieldErrors");

            if (fieldErrorsNode != null && fieldErrorsNode.isArray()) {
                for (JsonNode errorNode : fieldErrorsNode) {
                    String field = errorNode.get("field").asText();
                    String message = errorNode.get("message").asText();

                    fieldErrorsMap.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldErrorsMap;
    }

    private List<String> extractValidationMessages(Set<ConstraintViolation<PlayerNameDto>> violations) {
        List<String> messages = new ArrayList<>();
        for (ConstraintViolation<PlayerNameDto> violation : violations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }
}
