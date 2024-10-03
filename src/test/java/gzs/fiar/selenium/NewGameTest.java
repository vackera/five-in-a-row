package gzs.fiar.selenium;

import gzs.fiar.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class NewGameTest {

    @Autowired
    private String serverAddress;

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
    }

    @AfterEach
    void teardown() {

        driver.quit();
    }

    @Test
    void newGameTest() {

        driver.findElement(By.id("new-game-button")).click();

        String expected = (String) js.executeScript("return MESSAGES.NEW_GAME;");
        String actual = driver.findElement(By.cssSelector("#info-message")).getText();

        assertEquals(expected, actual);
    }

}
