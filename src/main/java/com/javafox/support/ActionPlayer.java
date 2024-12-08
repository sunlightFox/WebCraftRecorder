package com.javafox.support;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class ActionPlayer {

    private WebDriver driver;
    private WebDriverWait wait;

    public void playActions(String url, String inputFilePath, boolean optimizeInputs) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);

        try {
            driver.get(url);

            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            List<Action> actions = readActions(inputFilePath);

            if (optimizeInputs) {
                actions = optimizeConsecutiveInputs(actions);
            }

            for (Action action : actions) {
                try {
                    WebElement element = findElement(action.selector);
                    performAction(element, action);
                } catch (Exception e) {
                    System.err.println("Error performing action: " + action.type + " on " + action.selector);
                    e.printStackTrace();
                }

                Thread.sleep(1000); // 增加等待时间
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private List<Action> optimizeConsecutiveInputs(List<Action> actions) {
        List<Action> optimizedActions = new ArrayList<>();
        Map<String, Action> lastInputActions = new HashMap<>();

        for (Action action : actions) {
            if (action.type.equals("input")) {
                lastInputActions.put(action.selector, action);
            } else {
                optimizedActions.addAll(lastInputActions.values());
                lastInputActions.clear();
                optimizedActions.add(action);
            }
        }

        optimizedActions.addAll(lastInputActions.values());
        return optimizedActions;
    }

    private WebElement findElement(String selector) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
        } catch (TimeoutException e) {
            // 如果无法找到元素，尝试使用 JavaScript 定位
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (WebElement) js.executeScript(
                    "return document.querySelector('" + selector + "');"
            );
        }
    }

    private void performAction(WebElement element, Action action) {
        switch (action.type) {
            case "click":
                clickElement(element);
                break;
            case "input":
                inputText(element, action.value);
                break;
            case "change":
                changeElement(element, action.value);
                break;
        }
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // 如果元素被遮挡，尝试使用 JavaScript 点击
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
        }
    }

    private void inputText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    private void changeElement(WebElement element, String value) {
        if (element.getTagName().equalsIgnoreCase("select")) {
            // 处理 select 元素
            String[] options = value.split(",");
            for (String option : options) {
                element.findElement(By.cssSelector("option[value='" + option + "']")).click();
            }
        } else if (element.getAttribute("type").equalsIgnoreCase("checkbox")
                || element.getAttribute("type").equalsIgnoreCase("radio")) {
            // 处理 checkbox 和 radio
            boolean shouldBeChecked = Boolean.parseBoolean(value);
            if (element.isSelected() != shouldBeChecked) {
                clickElement(element);
            }
        }
    }

    private static List<Action> readActions(String filename) throws IOException {
        List<Action> actions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                actions.add(new Action(parts[0], parts[1], parts.length > 2 ? parts[2] : null));
            }
        }
        return actions;
    }

    private static class Action {
        String type;
        String selector;
        String value;

        Action(String type, String selector, String value) {
            this.type = type;
            this.selector = selector;
            this.value = value;
        }
    }
}