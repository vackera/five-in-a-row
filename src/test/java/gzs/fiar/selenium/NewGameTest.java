package gzs.fiar.selenium;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewGameTest {

    private final String webServerAddress = "http://localhost:8080";

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
    void newGameTest() {

        driver.get(webServerAddress);

        driver.manage().window().setSize(new Dimension(500, 1000));
        driver.findElement(By.id("new-game-button")).click();

        String expected = (String) js.executeScript("return MESSAGES.NEW_GAME;");
        String actual = driver.findElement(By.cssSelector("#info-message")).getText();

        assertEquals(expected, actual);
    }

}
