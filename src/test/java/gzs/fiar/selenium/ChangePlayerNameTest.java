package gzs.fiar.selenium;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gzs.fiar.config.TestConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Import(TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ChangePlayerNameTest {

    @Autowired
    private String serverAddress;

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
        driver.get(serverAddress);
        driver.manage().window().setSize(new Dimension(500, 1000));

        js.executeScript("clearTimeout(playerNameInfoTimer);");
        js.executeScript("clearTimeout(playerIconInfoTimer);");
        js.executeScript("clearTimeout(firstStepAlertTimer);");
        js.executeScript("hideInfoBar();");
    }

    @AfterEach
    void teardown() {

        driver.quit();
    }

    @Test
    void changePlayerNameTest_withValidInput() {

        String expected = (String) js.executeScript("return MESSAGES.PLAYER_NAME_CHANGED;");

        sendPlayerName("Test Player");
        waitUntilInfoMessageChanges();

        String actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    @Test
    void changePlayerNameTest_withInvalidInputs() {

        String expected = (String) js.executeScript("return MESSAGES.INVALID_PLAYER_NAME;");
        String actual;

        sendPlayerName("         ");
        waitUntilInfoMessageChanges();

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        waitUntilShakeStops();

        sendPlayerName("");
        waitUntilInfoMessageChanges();

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        waitUntilShakeStops();

        sendPlayerName("@$&#_!?");
        waitUntilInfoMessageChanges();

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
        //itt
        waitUntilShakeStops();

        sendPlayerName("Test_Pl@yer");
        waitUntilInfoMessageChanges();

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);

        waitUntilShakeStops();

        js.executeScript("document.getElementById('player-name').removeAttribute('maxlength');");

        sendPlayerName("Valid Name But Too Long");
        waitUntilInfoMessageChanges();

        actual = driver.findElement(By.cssSelector("#info-message")).getText();
        assertEquals(expected, actual);
    }

    private void waitUntilInfoMessageChanges() {

        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            String temp = driver.findElement(By.cssSelector("#info-message")).getText();
            return !temp.isEmpty();
        });
    }

    @Test
    void changePlayerNameTest_withInvalidInputs_disabledJavascriptPreCheck() {

        js.executeScript("isPlayerNameValid = function() { return true; }");
        js.executeScript("reloadPage = function() { }");
        js.executeScript("document.getElementById('player-name').removeAttribute('maxlength');");

        String checkedPlayerName;
        XHRResult xhrResult;
        List<String> expectedMessages;
        List<String> actualMessages;

        checkedPlayerName = "Pl@yerName_";
        xhrResult = getXHRResult(checkedPlayerName);

        expectedMessages = extractValidationMessages(xhrResult.violations());
        actualMessages = xhrResult.fieldErrors().get("name");
        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        checkedPlayerName = "PN";
        xhrResult = getXHRResult(checkedPlayerName);

        expectedMessages = extractValidationMessages(xhrResult.violations());
        actualMessages = xhrResult.fieldErrors().get("name");
        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        checkedPlayerName = "!?&@$ß";
        xhrResult = getXHRResult(checkedPlayerName);

        expectedMessages = extractValidationMessages(xhrResult.violations());
        actualMessages = xhrResult.fieldErrors().get("name");
        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        checkedPlayerName = "";
        xhrResult = getXHRResult(checkedPlayerName);

        expectedMessages = extractValidationMessages(xhrResult.violations());
        actualMessages = xhrResult.fieldErrors().get("name");
        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);

        checkedPlayerName = "Valid Name But Too Long";
        xhrResult = getXHRResult(checkedPlayerName);

        expectedMessages = extractValidationMessages(xhrResult.violations());
        actualMessages = xhrResult.fieldErrors().get("name");
        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    private XHRResult getXHRResult(String checkedPlayerName) {

        Set<ConstraintViolation<PlayerNameDto>> violations;
        PlayerNameDto playerNameDto;
        String xhrResponseData;
        Map<String, List<String>> fieldErrors;

        playerNameDto = new PlayerNameDto(checkedPlayerName);
        sendPlayerName(checkedPlayerName);

        waitUntilBackendResponses();

        xhrResponseData = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
        fieldErrors = parseFieldErrors(xhrResponseData);

        violations = validator.validate(playerNameDto);
        return new XHRResult(fieldErrors, violations);
    }

    private record XHRResult(Map<String, List<String>> fieldErrors, Set<ConstraintViolation<PlayerNameDto>> violations) {
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
            Map<String, Object> responseData = objectMapper.readValue(xhrResponseData, new TypeReference<Map<String, Object>>() {
            });

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

    private void waitUntilShakeStops() {

        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            String temp = driver.findElement(By.cssSelector("#player-name")).getText();
            return temp != null && temp.isEmpty();
        });
    }

    private void waitUntilBackendResponses() {

        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            String temp = (String) js.executeScript("return JSON.stringify(xhrResponseData);");
            return temp != null && !temp.equals("{}");
        });
    }
}
