package com.javafox;

import com.javafox.support.ActionPlayer;
import com.javafox.support.ActionRecorder;

public class WebCraftRecorder {
    public static void main(String[] args) {
        /*
        *  1、先下载chromedriver（查看当前chrome浏览器的版本号，然后使用以下地址改下版本好下载），下载好后替换下下一行代码的"chromedriver路径"
        *     linux64 https://storage.googleapis.com/chrome-for-testing-public/浏览器版本号/linux64/chromedriver-linux64.zip
        *     mac-arm64 https://storage.googleapis.com/chrome-for-testing-public/浏览器版本号/mac-arm64/chromedriver-mac-arm64.zip
        *     mac-x64   https://storage.googleapis.com/chrome-for-testing-public/浏览器版本号/mac-x64/chromedriver-mac-x64.zip
        *     win32   https://storage.googleapis.com/chrome-for-testing-public/浏览器版本号/win32/chromedriver-win32.zip
        *     win64   https://storage.googleapis.com/chrome-for-testing-public/浏览器版本号/win64/chromedriver-win64.zip
        *   例如
        *           System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
        * */
        System.setProperty("webdriver.chrome.driver", "chromedriver路径");
        String url = "https://www.baidu.com";
        String recordFilePath = "src/main/resources/actions.txt";
        // 录制,这里录制时间为输入的30秒
        ActionRecorder actionRecorder = new ActionRecorder();
        actionRecorder.recordActions(url, recordFilePath, 10);
        System.out.println("录制完成，请查看actions.json文件");

        // 播放
        ActionPlayer actionPlayer = new ActionPlayer();
        actionPlayer.playActions(url, recordFilePath,true);
        System.out.println("播放完成");
    }
}
