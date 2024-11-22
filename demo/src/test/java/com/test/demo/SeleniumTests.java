package com.test.demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumTests {
    @Test
    @DisplayName("Hi mon")
    void setupClass() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://store.steampowered.com/");

        Thread.sleep(2000);
        driver.quit();
    }
//    WebDriver driver = new
}
