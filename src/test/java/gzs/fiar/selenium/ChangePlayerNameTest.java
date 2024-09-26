package gzs.fiar.selenium;

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

class ChangePlayerNameTest {

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
        driver.get(webServerAddress);
        driver.manage().window().setSize(new Dimension(500, 1000));
    }

    @AfterEach
    void teardown() {

        driver.quit();
    }

    @Test
    void changePlayerNameTest_withValidInput() throws InterruptedException {

        Thread.sleep(300);
        sendPlayerName("Test Player");

        String expected = (String) js.executeScript("return MESSAGES.PLAYER_NAME_CHANGED;");
        String actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    @Test
    void changePlayerNameTest_withInvalidInputs() throws InterruptedException {

        String expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");

        Thread.sleep(300);
        sendPlayerName("Test_Pl@yer");
        Thread.sleep(100);

        String actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        Thread.sleep(300);
        sendPlayerName("");
        Thread.sleep(100);

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        Thread.sleep(300);
        sendPlayerName("  ");
        Thread.sleep(100);

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        js.executeScript("document.getElementById('player-name').removeAttribute('maxlength');");

        Thread.sleep(300);
        sendPlayerName("Valid Name But Too Long");
        Thread.sleep(100);

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    @Test
    void changePlayerNameTest_withInvalidInputs_disabledJavascriptPreCheck() throws InterruptedException {

        js.executeScript("isPlayerNameValid = function() { return true; }");
        js.executeScript("reloadPage = function() { }");
        js.executeScript("document.getElementById('player-name').removeAttribute('maxlength');");

        sendPlayerName("Pl@yerName_");

        String xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        Map<String, List<String>> fieldErrors = parseFieldErrors(xhrResponseData);

        PlayerNameDto playerNameDto = new PlayerNameDto("Pl@yerName_");
        Set<ConstraintViolation<PlayerNameDto>> violations = validator.validate(playerNameDto);

        List<String> expectedMessages = extractValidationMessages(violations);
        List<String> actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        Thread.sleep(200);

        sendPlayerName("PN");

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        playerNameDto = new PlayerNameDto("PN");
        violations = validator.validate(playerNameDto);

        expectedMessages = extractValidationMessages(violations);
        actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        Thread.sleep(200);

        sendPlayerName("");

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        playerNameDto = new PlayerNameDto("");
        violations = validator.validate(playerNameDto);

        expectedMessages = extractValidationMessages(violations);
        actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        Thread.sleep(200);

        sendPlayerName("Valid Name But Too Long");

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        playerNameDto = new PlayerNameDto("Valid Name But Too Long");
        violations = validator.validate(playerNameDto);

        expectedMessages = extractValidationMessages(violations);
        actualMessages = fieldErrors.get("name");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    private void sendPlayerName(String playerName) {

        driver.findElement(By.id("player-name")).click();
        driver.findElement(By.id("player-name")).sendKeys(playerName);
        driver.findElement(By.id("player-name")).sendKeys(Keys.ENTER);
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
