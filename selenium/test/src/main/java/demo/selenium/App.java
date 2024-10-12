package demo.selenium;

import java.util.Arrays;

public class App {
  public static void main(String[] args) {
    int[] demo = {1789, 2035, 1899, 1456, 2013, 1458, 2458, 1254, 1472, 2365,
        1456, 2165, 1457, 2456};
    Arrays.sort(demo);
    String[] demo2 = {"Java", "Python", "PHP", "C#", "C Programming", "C++"};
    Arrays.sort(demo2);
    System.out.println(Arrays.toString(demo));
    System.out.println(Arrays.toString(demo2));
  }
}
