package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.springconfig.CDBConfig;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDBConfig.class)
public class AddComputerServletTest {

    private WebDriver driver;
    JavascriptExecutor js;

    @Autowired
    ComputerService computerService;

    @Autowired
    CompanyService companyService;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.gecko.driver", "/opt/WebDriver/bin/geckodriver");
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void addCompleteTest() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("computerName")).click();
        driver.findElement(By.id("computerName")).sendKeys("Test encore et encore");
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        driver.findElement(By.id("companyId")).click();
        {
            WebElement dropdown = driver.findElement(By.id("companyId"));
            dropdown.findElement(By.xpath("//option[. = 'MOS Technology']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(8)")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
        Computer expected = new Computer.ComputerBuilder(computerService.getMaxId(), "Test encore et encore")
                .setDateIntroduction(LocalDateTime.of(2020, 6, 1, 0, 0))
                .setDateDiscontinuation(LocalDateTime.of(2020, 6, 4, 0, 0))
                .setEntreprise(companyService.getCompanyByName("MOS Technology").get()).build();
        assertEquals(expected, computerService.getComputerById(computerService.getMaxId()).get());
    }

    @Test
    public void addEmptyName() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("computerName")).click();
        driver.findElement(By.id("computerName")).sendKeys("Nothing");
        driver.findElement(By.id("computerName")).clear();
        assert (driver.findElement(By.id("cnErr")).isDisplayed());
        assertEquals("The computer's name cannot be empty", driver.findElement(By.id("cnErr")).getText());
    }

    @Test
    public void addIntroInputBoxTest() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-20");
        assert (driver.findElement(By.id("introErr")).isDisplayed());
        assertEquals("The discontinuation date is before the introduction date",
                driver.findElement(By.id("introErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).clear();
        assert (driver.findElement(By.id("introErr")).isDisplayed());
        assertEquals("An introduction date is needed if a discontinuation date is set",
                driver.findElement(By.id("introErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(driver -> !driver.findElement(By.id("introErr")).isDisplayed());
        assert (!driver.findElement(By.id("introErr")).isDisplayed());
    }

    @Test
    public void addDiscontInputBoxTest() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        assert (driver.findElement(By.id("discontErr")).isDisplayed());
        assertEquals("An introduction date is needed if a discontinuation date is set",
                driver.findElement(By.id("discontErr")).getText());

        driver.findElement(By.id("discontinued")).clear();
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-20");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(driver -> driver.findElement(By.id("discontErr")).isDisplayed());
        assert (driver.findElement(By.id("discontErr")).isDisplayed());
        assertEquals("The discontinuation date is before the introduction date",
                driver.findElement(By.id("discontErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(driver -> !driver.findElement(By.id("discontErr")).isDisplayed());
        assert (!driver.findElement(By.id("discontErr")).isDisplayed());
    }

}
