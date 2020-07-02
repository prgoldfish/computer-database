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
public class EditComputerServletTest {

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
    public void editComputerFullAndBackTest() {
        driver.get("http://localhost:8080/computer-database/ListComputers");
        firstEdit(); //Edite le premier ordinateur de la dernière page
        Computer expected = new Computer.ComputerBuilder(1, "Test Selenium")
                .setIntroduced(LocalDateTime.of(2020, 6, 1, 0, 0)).setDiscontinued(LocalDateTime.of(2020, 6, 7, 0, 0))
                .setEntreprise(companyService.getCompanyByName("Cray").get()).build();
        assertEquals(expected, computerService.getComputerById(1).get());

        editback(); //Ré-édite le même ordinateur pour remettre ses valeurs d'avant
        expected = new Computer.ComputerBuilder(1, "MacBook Pro 15.4 inch")
                .setIntroduced(LocalDateTime.of(2020, 6, 2, 0, 0)).setDiscontinued(LocalDateTime.of(2020, 6, 3, 0, 0))
                .setEntreprise(companyService.getCompanyByName("Apple Inc.").get()).build();
        assertEquals(expected, computerService.getComputerById(1).get());
    }

    private void editback() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver -> driver.findElement(By.linkText("Test Selenium"))).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(driver -> driver.findElement(By.id("computerName")))
                .click();
        driver.findElement(By.id("computerName")).clear();
        driver.findElement(By.id("computerName")).sendKeys("MacBook Pro 15.4 inch");
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).clear();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-02");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).clear();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-03");
        driver.findElement(By.id("companyId")).click();
        {
            WebElement dropdown = driver.findElement(By.id("companyId"));
            dropdown.findElement(By.xpath("//option[. = 'Apple Inc.']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
    }

    private void firstEdit() {
        driver.findElement(By.linkText("MacBook Pro 15.4 inch")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(driver -> driver.findElement(By.id("computerName")))
                .click();
        driver.findElement(By.id("computerName")).clear();
        driver.findElement(By.id("computerName")).sendKeys("Test Selenium");
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).clear();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).clear();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-07");
        driver.findElement(By.id("companyId")).click();
        {
            WebElement dropdown = driver.findElement(By.id("companyId"));
            dropdown.findElement(By.xpath("//option[. = 'Cray']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(31)")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
    }

    @Test
    public void editEmptyName() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("computerName")).click();
        driver.findElement(By.id("computerName")).sendKeys("Nothing");
        driver.findElement(By.id("computerName")).clear();
        assert (driver.findElement(By.id("cnErr")).isDisplayed());
        assertEquals("Le nom de l'ordinateur doit être renseigné", driver.findElement(By.id("cnErr")).getText());
    }

    @Test
    public void editIntroInputBoxTest() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-20");
        assert (driver.findElement(By.id("introErr")).isDisplayed());
        assertEquals("La date de discontinuation est avant la date d'introduction",
                driver.findElement(By.id("introErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).clear();
        assert (driver.findElement(By.id("introErr")).isDisplayed());
        assertEquals("Une date d'introduction est requise si une date de discontinuation est présente",
                driver.findElement(By.id("introErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(driver -> !driver.findElement(By.id("introErr")).isDisplayed());
        assert (!driver.findElement(By.id("introErr")).isDisplayed());
    }

    @Test
    public void editDiscontInputBoxTest() {
        driver.get("http://localhost:8080/computer-database/AddComputer");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        assert (driver.findElement(By.id("discontErr")).isDisplayed());
        assertEquals("Une date d'introduction est requise si une date de discontinuation est présente",
                driver.findElement(By.id("discontErr")).getText());

        driver.findElement(By.id("discontinued")).clear();
        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-20");
        driver.findElement(By.id("discontinued")).click();
        driver.findElement(By.id("discontinued")).sendKeys("2020-06-04");
        assert (driver.findElement(By.id("discontErr")).isDisplayed());
        assertEquals("La date de discontinuation est avant la date d'introduction",
                driver.findElement(By.id("discontErr")).getText());

        driver.findElement(By.id("introduced")).click();
        driver.findElement(By.id("introduced")).sendKeys("2020-06-01");
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(driver -> !driver.findElement(By.id("discontErr")).isDisplayed());
        assert (!driver.findElement(By.id("discontErr")).isDisplayed());
    }

}
