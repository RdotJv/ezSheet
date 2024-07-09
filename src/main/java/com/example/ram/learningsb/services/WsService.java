package com.example.ram.learningsb.services;

import java.io.*;
import java.net.URL;
import java.util.regex.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WsService {
    public ArrayList<String> resultLinks = new ArrayList<>();     //links kept in case user is dissatisfied
    private String url = "https://www.google.com/search?q=site:imslp.org+";
    private ChromeDriver driver;

    public ChromeDriver initDriver() {
        cleanDriver();
        System.out.println("opening driver");
        this.driver = new ChromeDriver();
        System.setProperty("webdriver.chrome.driver", "C:/Users/USER/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
        return driver;
    }

    @Scheduled(fixedRate = 600000) // 600000 milliseconds = 10 minutes
    public void cleanDriver() {
        if (driver != null) {
            System.out.println("closing driver");
            driver.quit();
            driver = null;
            resultLinks.clear();
        }
    }

    public void sortDlCount(HashMap<String, String> downloadsTracker){
        Set<Integer> tempkeyset = Set.copyOf(downloadsTracker.keySet()).stream().map(Integer::parseInt).collect(Collectors.toSet());
        ArrayList<Integer> intTemp = new ArrayList<>(tempkeyset);
        intTemp.sort(Comparator.reverseOrder());    //descending order arraylist of download counts
        for (int i: intTemp) {
            resultLinks.add(downloadsTracker.get(String.valueOf(i)));
        }
    }

    public void initLinksAndNav (String composerName, String sheetName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        driver.get(url+(composerName+sheetName).replace(" ", "%20"));   //google search
        List<WebElement> sheets = new ArrayList<>();
        int x = 0;
        while (true) {      //in the event that the google result picked is not desired, try the next result until elements required are found
            try {
                driver.findElements(By.partialLinkText("https://imslp.org")).get(x).click(); //find and click first result w imslp
                sheets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"tabScore1\"]/div"))); //all 'we' divs
                //includes all except the last 2 results (which are usually copyrighted)
                break;  //break if successful (no exceptions raised)
            } catch (Exception e) {
                driver.navigate().back();
                if (++x == 10) {
                    System.out.println("unrecognised webpage. Please email your search query and a screenshot of the page to me " +
                            "at ramjvandca@gmail.com");
                    cleanDriver();
                    return;
                }
            }
        }
        int n = 0;
        HashMap<String, String> downloadsTracker = new HashMap<>();  //making a record of every result's download count and their link
        for (int i=0; i<sheets.size(); i++) {
            String link = driver.findElement(By.xpath("//*[@id='tabScore1']/div[" + (++n) + "]/div/div/p/span/a")).getAttribute("href");
            String downloadCount = driver.findElement(By.xpath("//*[@id='tabScore1']/div[" + n + "]/div/div/p/span/span[4]/a")).getText();
            int z = 0;
            while (true) {
                try {
                    z++;
                    if (z>10) {break;}
                    downloadCount = driver.findElement(By.xpath("//*[@id='tabScore1']/div[" + n + "]/div/div/p/span/span["+(2+(z))+"]/a")).getText();
                    if (Objects.equals(downloadCount, "")) {continue;}
                    break;
                } catch (Exception e) {}
            }
            if (Objects.equals(downloadCount, "")) {continue;}

            downloadsTracker.put(downloadCount, link);

        }
        sortDlCount(downloadsTracker);  /* gives resultLinks the data it needs (an array of strings containing links sorted
                                         in descending order based on the number of downloads */
        System.out.println("resultlinks --------------------------"+resultLinks);
    }

    public String[] getPdfLink(int index, ArrayList<String> variations) {
        driver.get(variations.get(index));
        driver.findElement(By.xpath("//*[@id=\"file\"]/a/img")).click(); //go to downloads page
        try {
            driver.findElement(By.xpath("//*[@id=\"wiki-body\"]/div[2]/center/a")).click();  //accepting TOS
        } catch (Exception e) {
            System.out.println("THIS ERROR IS NOT A PROBLEM, PROGRAM ATTEMPTED TO ACCESS THE" +
                    "TOS ELEMENT AGAIN WHEN IT NO LONGER EXISTS ");
        }
        String linkForPdf;
        if (!Objects.equals(driver.findElement(By.xpath("/html")).getAttribute("class"), "client-js")) {
            /*some results do not have a wait-wall, so to combat this the program simply returns the current url if
              it has encountered the pdf early */
            linkForPdf = driver.getCurrentUrl();
        } else {
            linkForPdf = driver.findElement(By.xpath("//*[@id=\"sm_dl_wait\"]")).getAttribute("data-id");
            //bypass wait-wall
        }
        String pieceName = applyRegexOnLink(linkForPdf);
        return new String[]{pieceName,linkForPdf};
    }

    public String applyRegexOnLink(String linkForPdf) {
        Pattern pattern = Pattern.compile("(IMSLP\\d{1,7})-([^-]*)-(.*)\\.pdf");
        Matcher matcher = pattern.matcher(linkForPdf);
        if (!matcher.find()) {
            return null;
        } else {
            return matcher.group(3);
        }
    }

    public byte[] downloadPdf(String pieceName, String link) {
        try {
            URL url = new URL(link);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
            byte[] fin = fileOutputStream.toByteArray();
            fileOutputStream.close();
            inputStream.close();
            return fin;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("oh no no no no no no no god no");
        return new byte[0];
    }
    public String[] initialSearch(String composerName, String sheetName) {
        initDriver();
        initLinksAndNav(composerName, sheetName);
        return getPdfLink(0, resultLinks);
    }
}
