package com.backend.jobHub.service;

import com.backend.jobHub.entity.ScrapedData;
import com.backend.jobHub.repository.ScrapedDataRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LinkedInService {

    private  final ScrapedDataRepository scrapedDataRepository;
    private static final String COOKIES_FILE_PATH = "C:/Users/vasu/OneDrive/Documents/My Projects/jobHub/jobHub/src/main/resources/cookies.dat";


        public String scrapeLinkedIn(String keywords, String email) {
        System.setProperty("webdriver.chrome.driver", "C:/Users/vasu/OneDrive/Documents/My Projects/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("start-maximized");
        options.addArguments("user-agent=" + getRandomUserAgent());
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-infobars");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        Set<String> uniqueEmails = new HashSet<>();
        try {
            driver.get("https://www.linkedin.com/login");

            WebElement usernameField = driver.findElement(By.id("username"));
            WebElement passwordField = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Sign in')]"));
        usernameField.sendKeys("vasu.busatechlo@gmail.com");
        passwordField.sendKeys("Vasubella123@");
            loginButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("feed"));

            String searchUrl = "https://www.linkedin.com/search/results/content/?datePosted=%22past-24h%22&keywords=" + keywords + "&origin=FACETED_SEARCH&sortBy=%22date_posted%22";
            driver.get(searchUrl);

            int previousPostCount = 0;
            int currentPostCount = 0;
            int postLimit = 10;

            do {
                previousPostCount = currentPostCount;
                List<WebElement> posts = driver.findElements(By.cssSelector("div.feed-shared-update-v2"));
                currentPostCount += posts.size();


                extractEmailsFromPosts(posts, uniqueEmails, driver);
                if (currentPostCount >= postLimit) break;
                scrollDown(driver);
                randomDelay(500, 1500);

            } while (currentPostCount > previousPostCount);

            ScrapedData scrapedData = new ScrapedData();
            scrapedData.setExtractedEmails(new ArrayList<>(uniqueEmails));
            scrapedData.setKeywords(keywords);
            scrapedData.setDateOfSearch(LocalDateTime.now().toString());
            scrapedData.setUserEmail(email);
            scrapedDataRepository.save(scrapedData);

            return "Scraping successful, data saved.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred during scraping.";
        } finally {
            driver.quit();
        }
    }

    private static void scrollDown(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,1000)");
    }

    private static void extractEmailsFromPosts(List<WebElement> posts, Set<String> uniqueEmails, WebDriver driver) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        for (WebElement post : posts) {
            try {
                WebElement moreButton = post.findElement(By.xpath(".//button[contains(@class, 'see-more')]"));

                if (moreButton.isDisplayed()) {
                    moreButton.click();
                    randomDelay(500, 1000);
                }

                String postText = post.getText();
                Matcher matcher = emailPattern.matcher(postText);
                while (matcher.find()) {
                    String email = matcher.group();
                    uniqueEmails.add(email);
                    System.out.println("Email found: " + email);
                }
            } catch (Exception e) {
                System.err.println("An error occurred while extracting emails from post: " + e.getMessage());
            }
        }
    }

    private static void randomDelay(int min, int max) throws InterruptedException {
        Random random = new Random();
        int delay = random.nextInt(max - min + 1) + min;
        Thread.sleep(delay);
    }

    private static String getRandomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
                "Mozilla/5.0 (Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:91.0) Gecko/20100101 Firefox/91.0"
        };
        Random random = new Random();
        return userAgents[random.nextInt(userAgents.length)];
    }
//
//
//    public String scrapeLinkedIn(String keywords, String email) {
//        System.setProperty("webdriver.chrome.driver", "C:/Users/vasu/OneDrive/Documents/My Projects/chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.addArguments("start-maximized");
//        options.addArguments("user-agent=" + getRandomUserAgent());
//        options.addArguments("--disable-gpu");
//        options.addArguments("--disable-infobars");
//        options.addArguments("--window-size=1920,1080");
//
////        String proxy = getNextProxy();
////        if (proxy != null) {
////            options.addArguments("--proxy-server=" + proxy);
////            System.out.println("Using proxy: " + proxy);
////        }
//
//        WebDriver driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
//
//        Set<String> uniqueEmails = new HashSet<>();
//        int postLimit = 10;
//
//        try {
//            File cookieFile = new File(COOKIES_FILE_PATH);
//            if (cookieFile.exists()) {
//                loadCookies(driver);
//                driver.get("https://www.linkedin.com/feed/");
//            } else {
//                loginToLinkedIn(driver);
//                saveCookies(driver);
//            }
//
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            wait.until(ExpectedConditions.urlContains("feed"));
//
//            String searchUrl = "https://www.linkedin.com/search/results/content/?datePosted=%22past-24h%22&keywords=" + keywords + "&origin=FACETED_SEARCH&sortBy=%22date_posted%22";
//            driver.get(searchUrl);
//
//            int previousPostCount = 0;
//            int currentPostCount = 0;
//
//            do {
//                previousPostCount = currentPostCount;
//                List<WebElement> posts = driver.findElements(By.cssSelector("div.feed-shared-update-v2"));
//                currentPostCount += posts.size();
//
//                extractEmailsFromPosts(posts, uniqueEmails);
//
//                if (currentPostCount >= postLimit) break;
//                scrollDown(driver);
//                randomDelay(45, 60);
//
//            } while (currentPostCount > previousPostCount);
//
//            if (!uniqueEmails.isEmpty()) {
//                saveEmailsToDatabase(uniqueEmails, keywords, email);
//            }
//
//            return "Scraping successful, data saved.";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred during scraping.";
//        } finally {
//            driver.quit();
//        }
//    }
//
//    private void loginToLinkedIn(WebDriver driver) throws InterruptedException {
//        driver.get("https://www.linkedin.com/login");
//        WebElement usernameField = driver.findElement(By.id("username"));
//        WebElement passwordField = driver.findElement(By.id("password"));
//        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Sign in')]"));
//
//        usernameField.sendKeys("vasu.busatechlo@gmail.com");
//        passwordField.sendKeys("Vasubella123@");
//        randomDelay(1000, 3000);
//        loginButton.click();
//    }
//
//    private void saveCookies(WebDriver driver) {
//        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(COOKIES_FILE_PATH))) {
//            Set<Cookie> cookies = driver.manage().getCookies();
//            out.writeObject(cookies);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadCookies(WebDriver driver) {
//        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(COOKIES_FILE_PATH))) {
//            Set<Cookie> cookies = (Set<Cookie>) in.readObject();
//            for (Cookie cookie : cookies) {
//                driver.manage().addCookie(cookie);
//            }
//            driver.navigate().refresh();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void scrollDown(WebDriver driver) {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("window.scrollBy(0,1000)");
//    }
//
//    public void extractEmailsFromPosts(List<WebElement> posts, Set<String> uniqueEmails) {
//        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
//
//        for (WebElement post : posts) {
//            try {
//                WebElement moreButton = post.findElement(By.xpath(".//button[contains(@class, 'see-more')]"));
//                if (moreButton.isDisplayed()) {
//                    moreButton.click();
//                    randomDelay(500, 1000);
//                }
//
//                String postText = post.getText();
//                Matcher matcher = emailPattern.matcher(postText);
//
//                while (matcher.find()) {
//                    String email = matcher.group();
//                    uniqueEmails.add(email);
//                    System.out.println("Email found: " + email);
//                }
//            } catch (Exception e) {
//                System.err.println("An error occurred while extracting emails from post: " + e.getMessage());
//            }
//        }
//    }
//
//
//    private static void randomDelay(int minSeconds, int maxSeconds) throws InterruptedException {
//        Random random = new Random();
//        int minMillis = minSeconds * 1000;
//        int maxMillis = maxSeconds * 1000;
//        int delay = random.nextInt(maxMillis - minMillis + 1) + minMillis;
//        Thread.sleep(delay);
//    }
//
//
//    private static String getRandomUserAgent() {
//        String[] userAgents = {
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
//                "Mozilla/5.0 (Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:91.0) Gecko/20100101 Firefox/91.0"
//        };
//        Random random = new Random();
//        return userAgents[random.nextInt(userAgents.length)];
//    }
//
//
//    public void saveEmailsToDatabase(Set<String> emails, String keywords, String userEmail) {
//        ScrapedData scrapedData = new ScrapedData();
//        scrapedData.setExtractedEmails(new ArrayList<>(emails));
//        scrapedData.setKeywords(keywords);
//        scrapedData.setDateOfSearch(LocalDateTime.now().toString());
//        scrapedData.setUserEmail(userEmail);
//        scrapedDataRepository.save(scrapedData);
//    }


    //    private static String getNextProxy() {
//        String[] PROXY_LIST = {
//                "http://47.88.31.196:8080",
//                "http://114.129.2.82:8081",
//                "http://24.150.137.174:8080",
//                "http://54.93.47.161:8090",
//                "http://187.94.100.254:8080",
//                "http://160.86.242.23:8080",
//                "http://43.200.77.128:3128",
//                "http://138.201.246.148:1080",
//                "http://154.236.177.105:1976",
//                "http://154.9.227.204:8080",
//                "http://116.202.113.187:60606",
//                "http://199.195.253.14:1080",
//                "http://179.96.28.58:80",
//                "http://72.10.160.91:8167",
//                "http://72.10.160.172:9739",
//                "http://124.243.133.226:80",
//                "http://15.235.153.57:8089",
//                "http://67.43.227.226:30373",
//                "http://67.43.236.22:22079",
//                "http://198.24.188.138:37000",
//                "http://72.10.160.170:2657",
//                "http://72.10.160.173:29439",
//                "http://67.43.228.253:12915",
//                "http://103.237.144.232:1311",
//                "http://34.97.57.4:8561",
//                "http://34.97.154.179:8660",
//                "http://34.97.192.154:8561",
//                "http://222.108.214.168:8080",
//                "http://34.97.243.172:8561",
//                "http://129.226.193.16:3128",
//                "http://43.153.237.252:3128",
//                "http://34.97.52.66:8561",
//                "http://43.133.59.220:3128",
//                "http://43.134.121.40:3128",
//                "http://171.244.60.55:8080",
//                "http://34.97.78.175:8561",
//                "http://67.43.236.19:17293",
//                "http://34.97.68.25:8561",
//                "http://43.134.229.98:3128",
//                "http://43.153.207.93:3128",
//                "http://43.153.208.148:3128",
//                "http://43.134.68.153:3128",
//                "http://34.97.149.238:8561",
//                "http://43.134.32.184:3128",
//                "http://47.252.29.28:11222",
//                "http://213.199.44.86:8000",
//                "http://34.97.11.208:8561",
//                "http://43.134.33.254:3128",
//                "http://34.97.58.253:8561",
//                "http://34.97.176.53:8561",
//                "http://34.97.65.59:8561",
//                "http://34.97.72.201:8561",
//                "http://47.89.184.18:3128",
//                "http://195.189.70.51:3128",
//                "http://72.10.160.94:8355",
//                "http://67.43.227.227:11023",
//                "http://192.162.192.148:55443",
//                "http://103.107.182.16:25512",
//                "http://193.233.18.93:1080",
//                "http://67.43.228.254:2679",
//                "http://198.24.188.142:54793",
//                "http://67.43.236.20:10145",
//                "http://148.72.165.7:30127",
//                "http://171.228.137.54:10089",
//                "http://103.56.157.39:8080",
//                "http://168.119.214.223:60606",
//                "http://162.19.107.209:3128",
//                "http://115.77.20.123:2024",
//                "http://103.82.246.5:8080",
//                "http://20.27.86.185:8080",
//                "http://34.97.190.56:8561",
//                "http://116.97.62.167:5000",
//                "http://181.129.97.34:999",
//                "http://142.171.90.93:3128",
//                "http://24.192.227.234:8080",
//                "http://206.42.43.192:8080",
//                "http://8.211.194.78:9098",
//                "http://113.161.187.190:8080",
//                "http://190.110.35.106:999",
//                "http://185.30.144.222:8080",
//                "http://203.190.46.127:8090",
//                "http://45.174.79.95:999",
//                "http://60.49.152.31:8080",
//                "http://5.160.57.36:8080",
//                "http://202.154.36.181:8181",
//                "http://103.186.204.52:8089",
//                "http://18.181.154.135:8888",
//                "http://178.48.68.61:18080",
//                "http://103.106.219.114:1080",
//                "http://47.243.92.199:3128",
//                "http://45.182.191.58:8080",
//                "http://8.213.222.157:3128",
//                "http://47.91.65.23:3128",
//                "http://41.33.56.22:1976",
//                "http://182.252.70.220:8082",
//                "http://15.235.12.19:3128",
//                "http://144.86.187.47:3129",
//                "http://45.70.236.195:999",
//                "http://144.86.187.43:3129",
//                "http://192.9.237.224:3128"
//        };
//
//        Random random = new Random();
//        return PROXY_LIST[random.nextInt(PROXY_LIST.length)];
//    }

    public List<String> scrapedEmails(String email) {
        List<ScrapedData> scrapedDataList = scrapedDataRepository.findByUserEmailOrderByDateOfSearchDesc(email);
        if (!scrapedDataList.isEmpty()) return scrapedDataList.get(0).getExtractedEmails();
        return new ArrayList<>();
    }
}
