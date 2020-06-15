package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;

public class AddComputerServletTest {

    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    ComputerService computerService;
    CompanyService companyService;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.gecko.driver", "/opt/WebDriver/bin/geckodriver");
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
        computerService = new ComputerService(new ComputerDAO());
        companyService = new CompanyService(new CompanyDAO());
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void addComplete() {
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

}
