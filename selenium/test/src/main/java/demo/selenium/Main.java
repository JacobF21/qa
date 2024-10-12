package demo.selenium;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
  public static void main(String[] args) throws InterruptedException {

    Playwright playwright = Playwright.create();
    Page page = playwright.chromium()
        .launch(new BrowserType.LaunchOptions().setHeadless(false)).newPage();
    List<String> usernames = List.of("standard_user", "locked_out_user",
        "problem_user", "performance_glitch_user", "error_user", "visual_user");
    String username = "";
    String password = "secret_sauce";

    int count = 0;
    for (int i = 0; i < usernames.size(); i++) {
      username = usernames.get(i);
      page.navigate("https://www.saucedemo.com/");
      Thread.sleep(3000);
      page.waitForSelector("#user-name");
      page.fill("#user-name", usernames.get(i));
      Thread.sleep(3000);
      page.waitForSelector("#password");
      page.fill("#password", password);
      Thread.sleep(5000);

      switch (username) {
        case "standard_user":
          page.waitForSelector("#login-button");
          page.click("#login-button");
          page.waitForSelector(".inventory_list");
          if (page.url().contains("/inventory.html")) {
            count++;
            System.out.println("Case Pass for username: " + username);
          } else {
            System.out.println("Case Fail for username: " + username);
          }
          break;

        case "locked_out_user":
          page.waitForSelector("#login-button");
          page.click("#login-button");
          page.waitForSelector(".error-button");
          if (page.isVisible(".error-button")) {
            count++;
            System.out.println("Case Pass for username: " + username);

          } else {
            System.out.println("Case Fail for username: " + username);
          }
          break;

        case "problem_user":
          page.waitForSelector("#login-button");
          page.click("#login-button");
          page.waitForSelector(".inventory_list");
          List<ElementHandle> images =
              page.querySelectorAll("a[id^='item_'] > .inventory_item_img");
          boolean allImagesCorrect = true;
          for (ElementHandle image : images) {
            String src = image.getAttribute("src");
            if (!src.equals("/static/media/sl-404.168b1cce.jpg")) {
              allImagesCorrect = false;
            }
          }
          if (allImagesCorrect) {
            count++;
            System.out.println("Case Pass for username: " + username);
          } else {
            System.out.println("Case Fail for username: " + username);
          }
          break;

        case "performance_glitch_user":
          page.waitForSelector("#login-button");
          long startTime = System.currentTimeMillis();
          page.click("#login-button");
          page.waitForLoadState(LoadState.LOAD);
          long endTime = System.currentTimeMillis();
          long responseTime = endTime - startTime;
          if (responseTime > 5000) {
            count++;
            System.out.println("Case Pass for username: " + username);
            System.out
                .println("Response time: " + responseTime + " milliseconds");
          } else {
            System.out.println("Case Fail for username: " + username);
            System.out
                .println("Response time: " + responseTime + " milliseconds");
          }
          break;

        case "error_user":
          final boolean[] result = {false};
          page.waitForSelector("#login-button");
          page.click("#login-button");
          page.onDialog(dialog -> {
            System.out.println("Dialog message: " + dialog.message()); // Print the alert message
            if ("Sorting is broken! This error has been reported to Backtrace."
                .equals(dialog.message())) {
                result[0] = true;
            }
            dialog.accept(); // Accept the alert
          });
          page.selectOption(".product_sort_container", "az");
          if (result[0] == true) {
            count++;
            System.out.println("Case Pass for username: " + username);
        } else {
            System.out.println("Case Fail for username: " + username);
        }
        break;

        default:
          break;
      }

      // page.waitForSelector(".bm-burger-button");
      // page.click(".bm-burger-button");
      // page.waitForSelector(".bm-item.menu-item");
      // page.waitForSelector("#about_sidebar_link");
      // page.click("#about_sidebar_link");

      // Thread.sleep(10000);
    }
    System.out.println(count + " login test case success");
    playwright.close();
  }
}
