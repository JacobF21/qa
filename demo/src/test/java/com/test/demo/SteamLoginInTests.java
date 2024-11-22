package com.test.demo;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class SteamLoginInTests {

    private Playwright playwright;
    private Browser browser;
    private static BrowserContext context;
    private Page page;
    private GmailService gmailService;

    @BeforeClass
    public void setUp() throws GeneralSecurityException, IOException {
        // Initialize Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(50)); 

        // Ensure thße directory for saving videos exists
        Path videoDir = Paths.get("videos/");
        if (!Files.exists(videoDir)) {
            Files.createDirectories(videoDir);
        }

        // Create a new context with video recording enabled
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(videoDir).setRecordVideoSize(1280, 720));

        page = context.newPage();

        Map<String, String> headers = new HashMap<>();
        headers.put("sec-ch-ua",
                "\"Chromium\";v=\"130\", \"Google Chrome\";v=\"130\", \"Not?A_Brand\";v=\"99\"");
        page.setExtraHTTPHeaders(headers);

        // Initialize GmailService
        gmailService = new GmailService();

        // Initialize ExtentReports
        // ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");
        // htmlReporter.config().setTheme(Theme.STANDARD);
        // htmlReporter.config().setDocumentTitle("Steam Login Tests");
        // htmlReporter.config().setReportName("Steam Login Test Report");
    
        // extent = new ExtentReports();
        // extent.attachReporter(htmlReporter);
        // extent.setSystemInfo("Tester", "Your Name");
    }

    @Test
    public void testFailedLogin() throws IOException, InterruptedException {
        // Navigate to the login page
        page.navigate(
                "https://steamcommunity.com/login/home/?goto=login%2Fhome%2F%3Fgoto%3D%252Fprofiles%252F76561199800523039%252Fhome");

        // Perform login steps with incorrect password
        page.fill("input._2GBWeup5cttgbTw8FM3tfx[type='text']", "qa_demo");
        Thread.sleep(500);
        page.fill("input._2GBWeup5cttgbTw8FM3tfx[type='password']",
                "WrongPassword123!");
        Thread.sleep(700);
        page.click("button.DjSvCZoKKfoNSmarsEcTS[type='submit']");

        // Wait for error message
        page.waitForSelector("div._1W_6HXiG4JJ0By1qN_0fGZ");

        // Verify that the error message is displayed
        assertTrue(page.isVisible("div._1W_6HXiG4JJ0By1qN_0fGZ"));
    }

    @Test
    public void testLogin() throws IOException, InterruptedException {
        // Navigate to the login page
        page.navigate(
                "https://steamcommunity.com/login/home/?goto=login%2Fhome%2F%3Fgoto%3D%252Fprofiles%252F76561199800523039%252Fhome");

        // Perform login steps
        page.fill("input._2GBWeup5cttgbTw8FM3tfx[type='text']", "qa_demo");
        Thread.sleep(500);
        page.fill("input._2GBWeup5cttgbTw8FM3tfx[type='password']",
                "Admin1234!");
        Thread.sleep(700);
        page.click("button.DjSvCZoKKfoNSmarsEcTS[type='submit']");

        // Wait for OTP input field to be visible
        page.waitForSelector("div._1gzkmmy_XA39rp9MtxJfZJ.Panel.Focusable");
        Thread.sleep(10000);

        // Retrieve OTP from Gmail
        String otp = gmailService.retrieveOTPFromGmail();
        System.out.println("OTP: " + otp);

        // String otp ="12345";

        // Input OTP into the field
        if (otp != null && otp.length() == 5) {
            for (int i = 0; i < otp.length(); i++) {
                page.fill("input._3xcXqLVteTNHmk-gh9W65d.Focusable:nth-of-type("
                        + (i + 1) + ")", String.valueOf(otp.charAt(i)));
            }
        } else {
            System.out.println("No OTP found or OTP length is incorrect.");
        }

        page.waitForURL(
                url -> url.contains("https://steamcommunity.com/profiles/"));
        assertTrue(page.url().contains("https://steamcommunity.com/profiles/"));
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
