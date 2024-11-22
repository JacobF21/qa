package com.test.demo;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.RequestOptions;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.HCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteamSignInTests {

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private static final String API_KEY = "cf1d3b9ce5babdcc686b01c975a8a0f4";
    private static final Logger logger =
            LoggerFactory.getLogger(SteamSignInTests.class);

    @BeforeAll
    public static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    public void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    public void closeContext() {
        context.close();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testSignIn() throws Exception {

        // Navigate to the Steam sign-in page
        page.navigate(
                "https://store.steampowered.com/join/?redir=%3Fsnr%3D1_60_4__global-header&snr=1_60_4__62");

        // Enter the email address
        page.fill("#email", "jacob.tang.igsl@gmail.com");

        // Enter the confirm email address
        page.fill("#reenter_email", "jacob.tang.igsl@gmail.com");

        // // Placeholder for CAPTCHA handling
        // String captchaSolution = solveCaptcha();
        // logger.info("CAPTCHA Solution: {}", captchaSolution);
        // // Find the textarea with name="h-captcha-response" and input the CAPTCHA solution
        // FrameLocator captchaFrameLocator = page.frameLocator(
        //         "iframe[title='Widget containing checkbox for hCaptcha security challenge']");
        // // captchaFrameLocator.locator("textarea[name='data-hcaptcha-response']")
        // //         .fill(captchaSolution);
        // captchaFrameLocator.locator("iframe").evaluate("frame => frame.setAttribute('data-hcaptcha-response', arguments[0])", captchaSolution);
        // // Use JavaScript to set the data-hcaptcha-response attribute

        ElementHandle captchaFrame = page.waitForSelector("iframe[src*='hcaptcha.com']");

        // Switch to the CAPTCHA iframe
        Frame captchaFrameContent = captchaFrame.contentFrame();

        // Use 2Captcha to solve the CAPTCHA
        String captchaSolution = solveCaptcha();



                APIRequestContext request = playwright.request().newContext();
        APIResponse response = request.post("https://api.hcaptcha.com/checkcaptcha/"+captchaSolution, 
            RequestOptions.create().setHeader("accept", "*/*")
            .setHeader("accept-encoding", "gzip, deflate, br, zstd")
            .setHeader("accept-language", "en-GB,en;q=0.9")
            .setHeader("content-type", "application/json;charset=UTF-8")
            .setHeader("origin", "https://newassets.hcaptcha.com")
            .setHeader("referer", "https://newassets.hcaptcha.com/")
            .setHeader("sec-ch-ua", "\"Not?A_Brand\";v=\"99\", \"Chromium\";v=\"130\"")
            .setHeader("sec-ch-ua-mobile", "?0")
            .setHeader("sec-ch-ua-platform", "\"macOS\"")
            .setHeader("sec-fetch-dest", "empty")
            .setHeader("sec-fetch-mode", "cors")
            .setHeader("sec-fetch-site", "same-site")
            .setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
            .setData("{\"data\":\"" + captchaSolution + "\"}"));;

        if (response.ok()) {
            System.out.println("CAPTCHA verification successful!");
        } else {
            System.out.println("CAPTCHA verification failed!");
        }

        // Thread.sleep(5000000);

        // ElementHandle captchaInput = captchaFrameContent.waitForSelector("textarea[name='data-hcaptcha-response']");
        // captchaInput.evaluate("(input, captchaResponse) => { input.value = captchaResponse; }", captchaSolution);
        //Locate and check the agreement checkbox
        page.check("#i_agree_check");

        //Locate and click the sign-in button
        page.click("#createAccountButton");

        //Verify sign-in by checking for a specific element that appears upon successful login
        page.waitForSelector("#successful_login_element_id",
                new Page.WaitForSelectorOptions().setTimeout(1000000));
        System.out.println("Sign-in successful!");
    }

    // private static String solveCaptcha() throws Exception {
    // TwoCaptcha solver = new TwoCaptcha(API_KEY);
    // HCaptcha captcha = new HCaptcha();
    // captcha.setSiteKey("f7de0da3-3303-44e8-ab48-fa32ff8ccc7b"); // Replace with the actual site key
    // captcha.setUrl("https://store.steampowered.com/login/");
    // try {
    // solver.solve(captcha);
    // System.out.println("Captcha solved: " + captcha.getCode());
    // } catch (Exception e) {
    // System.out.println("Error occurred: " + e.getMessage());
    // }
    // return captcha.getCode();ß
    // }
    private static String solveCaptcha() throws Exception {
        TwoCaptcha solver = new TwoCaptcha(API_KEY);
        HCaptcha captcha = new HCaptcha();
        captcha.setSiteKey("e18a349a-46c2-46a0-87a8-74be79345c92");
        captcha.setUrl(
                "https://store.steampowered.com/join/?redir=%3Fsnr%3D1_60_4__global-header&snr=1_60_4__62");

        try {
            solver.solve(captcha);
            return captcha.getCode();
        } catch (Exception e) {
            throw new Exception("Error occurred: " + e.getMessage());
        }
    }
}
