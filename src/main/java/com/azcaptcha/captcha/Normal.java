package com.azcaptcha.captcha;

import java.io.File;

public class Normal extends Captcha {

    public Normal() {
        super();
    }

    public Normal(String filePath) {
        this(new File(filePath));
    }

    public Normal(File file) {
        this();
        setFile(file);
    }

    public void setFile(String filePath) {
        setFile(new File(filePath));
    }

    public void setFile(File file) {
        files.put("file", file);
    }

    public void setBase64(String base64) {
        params.put("body", base64);
    }

    public void setHintText(String hintText) {
        params.put("textinstructions", hintText);
    }

    public void setHintImg(String base64) {
        params.put("imginstructions", base64);
    }

    public void setHintImg(File hintImg) {
        files.put("imginstructions", hintImg);
    }

}
