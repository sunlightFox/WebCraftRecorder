package com.javafox.support;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ActionRecorder {

    /**
     * 记录页面操作
     *
     * @param url          要记录操作的页面URL
     * @param saveFilePath 保存操作的文件路径
     * @param recordTimeout 记录操作的超时时间（秒）
     */
    public void recordActions(String url, String saveFilePath, long recordTimeout) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            driver.get(url); // 替换为实际的URL

            // 等待页面加载完成
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            // 注入JavaScript以监听点击和输入事件
            String script =
                    "window.seleniumActions = window.seleniumActions || [];" +
                            "(function() {" +
                            "function getSelector(element) {" +
                            "if (element.id) return '#' + element.id;" +
                            "if (element.className) {" +
                            "var classes = element.className.split(' ').filter(Boolean).map(c => '.' + c).join('');" +
                            "if (classes && document.querySelectorAll(classes).length === 1) return classes;" +
                            "}" +
                            "var path = [];" +
                            "while (element.nodeType === Node.ELEMENT_NODE) {" +
                            "var selector = element.nodeName.toLowerCase();" +
                            "if (element.id) {" +
                            "selector += '#' + element.id;" +
                            "path.unshift(selector);" +
                            "break;" +
                            "} else {" +
                            "var sibling = element;" +
                            "var nth = 1;" +
                            "while (sibling = sibling.previousElementSibling) {" +
                            "if (sibling.nodeName.toLowerCase() === selector) nth++;" +
                            "}" +
                            "if (nth !== 1) selector += ':nth-of-type(' + nth + ')';" +
                            "}" +
                            "path.unshift(selector);" +
                            "element = element.parentNode;" +
                            "}" +
                            "return path.join(' > ');" +
                            "}" +
                            "function recordAction(type, element, value) {" +
                            "var selector = getSelector(element);" +
                            "var data = type + ',' + selector;" +
                            "if (value !== undefined) data += ',' + value;" +
                            "window.seleniumActions.push(data);" +
                            "}" +
                            "document.addEventListener('click', function(e) {" +
                            "recordAction('click', e.target);" +
                            "}, true);" +
                            "document.addEventListener('input', function(e) {" +
                            "recordAction('input', e.target, e.target.value);" +
                            "}, true);" +
                            "document.addEventListener('change', function(e) {" +
                            "if (e.target.type === 'checkbox' || e.target.type === 'radio') {" +
                            "recordAction('change', e.target, e.target.checked);" +
                            "} else if (e.target.type === 'select-one' || e.target.type === 'select-multiple') {" +
                            "var selectedOptions = Array.from(e.target.selectedOptions).map(opt => opt.value).join(',');" +
                            "recordAction('change', e.target, selectedOptions);" +
                            "}" +
                            "}, true);" +
                            "})();";

            ((JavascriptExecutor) driver).executeScript(script);

            System.out.println("JavaScript injected successfully. Recording actions for 30 seconds...");

            // 等待用户操作
            Thread.sleep(recordTimeout * 1000);

            // 获取记录的操作
            List<String> actions = (List<String>) ((JavascriptExecutor) driver).executeScript("return window.seleniumActions;");

            System.out.println("Recorded actions: " + actions);

            // 将操作写入文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath))) {
                for (String action : actions) {
                    writer.write(action);
                    writer.newLine();
                }
            }

            System.out.println("Actions saved to file successfully.");

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (WebDriverException e) {
            System.err.println("WebDriver error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}