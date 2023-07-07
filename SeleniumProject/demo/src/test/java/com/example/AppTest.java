package com.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppTest {
    private WebDriver driver;
    private String websiteUrl = "https://automationexercise.com/";
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\webdriver\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().window().maximize();
        driver.get(websiteUrl);
        assertCurrentUrl(websiteUrl);
        assertPageTitle("Automation Exercise");
    }

    @Test
    public void addProductsToCartAndCheckCartPage() {
        List<WebElement> itemElements = driver.findElements(By.cssSelector("div.product-image-wrapper"));
        if (itemElements.isEmpty()) {
            System.out.println("No items found");
            return;
        }

        Collections.shuffle(itemElements);

        Actions actions = new Actions(driver);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        for (int i = 0; i < 4; i++) {
            WebElement itemElement = itemElements.get(i);
            scrollIntoView(itemElement);
            String productText = getProductText(itemElement);
            System.out.println("Selected item: " + productText);
            actions.moveToElement(itemElement).perform();
            WebElement addToCartButton = itemElement.findElement(By.cssSelector("a.add-to-cart"));
            jsExecutor.executeScript("arguments[0].click();", addToCartButton);
            By successModalSelector = By.className("modal-content");
            wait.until(ExpectedConditions.visibilityOfElementLocated(successModalSelector));
            if (i < 3) {
                clickContinueShoppingButton();
                sleep(5);
            }
        }

        WebElement viewCartLink = driver
                .findElement(By.xpath("//p[@class='text-center']/a/u[contains(text(),'View Cart')]"));
        viewCartLink.click();
        validateCartPage();
        clickProceedToCheckout();
        clickRegisterLoginLink();
        loginWithEmail("testemail1@gmail.com", "P@ssword1");
        validateNavigationToWebsiteURL();
        clickCartIcon();
        clickProceedToCheckoutButton();
        validateAddressDetails();
        clickPlaceOrderButton();
        validatePaymentPage();
        enterCardDetails("John Doe", "1234567890123456", "123", "12", "2024");
        clickPayAndConfirmOrderButton();
        validateOrderConfirmation();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void assertCurrentUrl(String expectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, expectedUrl, "URL is incorrect");
    }

    private void assertPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "Incorrect page title");
    }

    private void scrollIntoView(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private String getProductText(WebElement itemElement) {
        return itemElement.findElement(By.tagName("p")).getText();
    }

    private void clickContinueShoppingButton() {
        By continueShoppingButtonSelector = By.xpath("//button[contains(.,'Continue Shopping')]");
        WebElement continueShoppingButton = wait
                .until(ExpectedConditions.elementToBeClickable(continueShoppingButtonSelector));
        continueShoppingButton.click();
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void validateCartPage() {
        WebElement cartTable = driver.findElement(By.id("cart_info_table"));
        WebElement tableHeader = cartTable.findElement(By.tagName("thead"));
        String expectedHeader = "Item Description Price Quantity Total";
        String actualHeader = tableHeader.getText();
        Assert.assertEquals(actualHeader, expectedHeader, "Table headers are incorrect");

        List<WebElement> productRows = cartTable.findElements(By.xpath("//tr[starts-with(@id, 'product-')]"));
        for (WebElement productRow : productRows) {
            String productName = productRow.findElement(By.tagName("h4")).getText();
            String productPrice = productRow.findElement(By.className("cart_price")).getText();
            String productQuantity = productRow.findElement(By.className("cart_quantity")).getText();
            String productTotal = productRow.findElement(By.className("cart_total")).getText();
            System.out.println("Product Name: " + productName);
            System.out.println("Product Price: " + productPrice);
            System.out.println("Product Quantity: " + productQuantity);
            System.out.println("Product Total: " + productTotal);
        }
    }

    private void clickProceedToCheckout() {
        WebElement proceedToCheckoutButton = driver.findElement(By.cssSelector("a.btn.btn-default.check_out"));
        proceedToCheckoutButton.click();
        By modalSelector = By.className("modal-content");
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalSelector));
        WebElement modal = driver.findElement(modalSelector);
        Assert.assertTrue(modal.isDisplayed(), "Modal is not displayed");
    }

    private void clickRegisterLoginLink() {
        // Find and click the "Register / Login" link
        WebElement registerLoginLink = driver.findElement(By.xpath("//u[contains(text(),'Register / Login')]"));
        registerLoginLink.click();
        System.out.println("Clicked on Register / Login link");
    }

    private void loginWithEmail(String email, String password) {
        WebElement emailInput = driver.findElement(By.cssSelector("input[data-qa='login-email']"));
        emailInput.sendKeys(email);
        WebElement passwordInput = driver.findElement(By.cssSelector("input[data-qa='login-password']"));
        passwordInput.sendKeys(password);
        WebElement loginButton = driver.findElement(By.xpath("//button[@data-qa='login-button']"));
        loginButton.click();
    }

    private void validateNavigationToWebsiteURL() {
        wait.until(ExpectedConditions.urlToBe(websiteUrl));
        assertCurrentUrl(websiteUrl);
        System.out.println("Successfully logged in and navigated to the website URL");
    }

    private void clickCartIcon() {
        WebElement cartIcon = driver.findElement(By.xpath("//a[contains(@href,'/view_cart')]"));
        cartIcon.click();
        System.out.println("Clicked on the cart icon");
    }

    private void clickProceedToCheckoutButton() {
        WebElement proceedToCheckoutButton = driver
                .findElement(
                        By.xpath("//a[@class='btn btn-default check_out' and contains(text(),'Proceed To Checkout')]"));
        proceedToCheckoutButton.click();
        System.out.println("Clicked on Proceed to Checkout button");
    }

    private void validateAddressDetails() {
        By addressDetailsSelector = By.xpath("//h2[@class='heading' and contains(text(),'Address Details')]");
        WebElement addressDetailsHeading = wait
                .until(ExpectedConditions.visibilityOfElementLocated(addressDetailsSelector));
        Assert.assertTrue(addressDetailsHeading.isDisplayed(), "Address Details heading is not displayed");
        System.out.println("Address Details heading is displayed");
    }

    private void clickPlaceOrderButton() {
        WebElement placeOrderButton = driver
                .findElement(By.xpath("//a[@href='/payment' and @class='btn btn-default check_out']"));
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].click();", placeOrderButton);
        System.out.println("Clicked on Place Order button");

    }

    private void validatePaymentPage() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        By paymentHeadingSelector = By.xpath("//h2[@class='heading' and contains(text(),'Payment')]");
        WebElement paymentHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(paymentHeadingSelector));
        Assert.assertTrue(paymentHeading.isDisplayed(), "Payment page heading is not displayed");
        System.out.println("Validated Payment page");
    }

    private void enterCardDetails(String nameOnCard, String cardNumber, String cvv, String expiryMonth,
            String expiryYear) {
        WebElement nameOnCardInput = driver.findElement(By.cssSelector("input[data-qa='name-on-card']"));
        nameOnCardInput.sendKeys(nameOnCard);
        WebElement cardNumberInput = driver.findElement(By.cssSelector("input[data-qa='card-number']"));
        cardNumberInput.sendKeys(cardNumber);
        WebElement cvvInput = driver.findElement(By.cssSelector("input[data-qa='cvc']"));
        cvvInput.sendKeys(cvv);
        WebElement expiryMonthInput = driver.findElement(By.cssSelector("input[data-qa='expiry-month']"));
        expiryMonthInput.sendKeys(expiryMonth);
        WebElement expiryYearInput = driver.findElement(By.cssSelector("input[data-qa='expiry-year']"));
        expiryYearInput.sendKeys(expiryYear);
        System.out.println("Entered card details");
    }

    private void clickPayAndConfirmOrderButton() {
        WebElement payButton = driver.findElement(By.cssSelector("button[data-qa='pay-button']"));
        payButton.click();
        System.out.println("Clicked on Pay and Confirm Order button");
    }

    private void validateOrderConfirmation() {
        By orderPlacedSelector = By.xpath("//h2[@class='title text-center' and @data-qa='order-placed']/b");

        WebDriverWait wait = new WebDriverWait(driver, 10);

        WebElement orderPlacedElement = wait.until(ExpectedConditions.visibilityOfElementLocated(orderPlacedSelector));
        Assert.assertEquals(orderPlacedElement.getText(), "ORDER PLACED!", "Order Placed message is incorrect");

        System.out.println("Order confirmation validated");
    }

}
