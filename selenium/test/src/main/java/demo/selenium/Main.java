package demo.selenium;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Playwright playwright = Playwright.create();
        Page page = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)).newPage();
        page.navigate("https://www.saucedemo.com/");
        List<String> usernames = List.of("standard_user", "locked_out_user","problem_user","performance_glitch_user","error_user","visual_user");
        String password = "secret_sauce";
        int count = 0;
        System.out.println(usernames.size());
        for(int i =0;i<usernames.size();i++){
            page.navigate("https://www.saucedemo.com/");
            Thread.sleep(3000);
            page.waitForSelector("#user-name");
            page.fill("#user-name", usernames.get(i));
            Thread.sleep(5000);
            page.waitForSelector("#password");
            page.fill("#password", password);
            Thread.sleep(5000);
            page.waitForSelector("#login-button");
            page.click("#login-button");
    
            Thread.sleep(2000);
            for (int j=0; j<5; j++) {
                page.keyboard().press("PageDown", new Keyboard.PressOptions().setDelay(1000));
            }
            for (int k=0; k<5; k++) {
                page.keyboard().press("PageUp", new Keyboard.PressOptions().setDelay(1000));
            }
    
            Thread.sleep(1000);
    
            page.waitForSelector(".bm-burger-button");
            page.click(".bm-burger-button");
            page.waitForSelector(".bm-item.menu-item");
            page.waitForSelector("#about_sidebar_link");
            page.click("#about_sidebar_link");
    
            Thread.sleep(10000);
            count++;
        }
        System.out.println(count + "usernamae can be successfully login");
        playwright.close();


    }
}
