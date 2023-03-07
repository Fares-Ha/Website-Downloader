package models;


import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;

public class URL_validator {



    public Boolean isUrl(String s)
    {
        try {
            new URL(s);
            return true;
        } catch (MalformedURLException e) {

        }
        return false;
    }



    public int getResponseCode(String urlString) {
        URL u = null;
        HttpURLConnection huc = null;

        try {
            u = new URL(urlString);
        } catch (MalformedURLException e) {System.err.println("unvalide url");}

        try {
            huc = (HttpURLConnection)  u.openConnection();
        } catch (IOException e) {System.err.println("err with openning connection");}

        try {
            huc.setRequestMethod("GET");
        } catch (ProtocolException e) {System.err.println("err with set request method");}

        try {
            huc.connect();
        } catch (IOException e) {System.out.println("err with connection");}

        try {
            return huc.getResponseCode();
        } catch (IOException e) {return 404;}
    }


    public String getURLFromCustomURL(String customUrl)
    {
        System.setProperty("webdriver.chrome.driver", "webDrivers\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        WebDriver driver =  new ChromeDriver(options);
        driver.get("http://"+customUrl);
        String currentURL=driver.getCurrentUrl();
        System.out.println(currentURL);

        /// driver.getPageSource().length();
        //  String test= driver.getPageSource().toString();
        driver.quit();
        return currentURL;
    }

}
