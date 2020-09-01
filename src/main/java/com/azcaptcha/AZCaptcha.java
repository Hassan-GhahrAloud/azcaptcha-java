package com.azcaptcha;

import com.azcaptcha.captcha.Captcha;
import com.azcaptcha.captcha.ReCaptcha;
import com.azcaptcha.exceptions.ApiException;
import com.azcaptcha.exceptions.NetworkException;
import com.azcaptcha.exceptions.TimeoutException;
import com.azcaptcha.exceptions.ValidationException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class AZCaptcha
 */
public class AZCaptcha {

    /**
     * API KEY
     */
    private String apiKey;

    /**
     * ID of software developer. Developers who integrated their software
     * with our service get reward: 10% of spendings of their software users.
     */
    private int softId;

    /**
     * URL to which the result will be sent
     */
    private String callback;

    /**
     * How long should wait for captcha result (in seconds)
     */
    private int defaultTimeout = 120;

    /**
     * How long should wait for recaptcha result (in seconds)
     */
    private int recaptchaTimeout = 600;

    /**
     * How often do requests to `/res.php` should be made
     * in order to check if a result is ready (in seconds)
     */
    private int pollingInterval = 10;

    /**
     * Helps to understand if there is need of waiting
     * for result or not (because callback was used)
     */
    private boolean lastCaptchaHasCallback;

    /**
     * Network client
     */
    private ApiClient apiClient;

    /**
     * AZCaptcha constructor
     */
    public AZCaptcha() {
        this.apiClient = new ApiClient();
    }

    /**
     * AZCaptcha constructor
     *
     * @param apiKey  your API key
     */
    public AZCaptcha(String apiKey) {
        this();
        setApiKey(apiKey);
    }

    /**
     * @param apiKey  your API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param softId  ID of software developer.
     */
    public void setSoftId(int softId) {
        this.softId = softId;
    }

    /**
     * @param callback URL for pingback (callback) response that will be sent when captcha is solved.
     */
    public void setCallback(String callback) {
        this.callback = callback;
    }

    /**
     * @param timeout  timeout
     */
    public void setDefaultTimeout(int timeout) {
        this.defaultTimeout = timeout;
    }

    /**
     * @param timeout  timeout
     */
    public void setRecaptchaTimeout(int timeout) {
        this.recaptchaTimeout = timeout;
    }

    /**
     * @param interval  interval
     */
    public void setPollingInterval(int interval) {
        this.pollingInterval = interval;
    }

    /**
     * @param apiClient  apiClient
     */
    public void setHttpClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Sends captcha to `/in.php` and waits for it's result.
     * This helper can be used instead of manual using of `send` and `getResult` functions.
     *
     * @param captcha  captcha
     * @throws Exception  Exception
     */
    public void solve(Captcha captcha) throws Exception {
        Map<String, Integer> waitOptions = new HashMap<>();

        if (captcha instanceof ReCaptcha) {
            waitOptions.put("timeout", recaptchaTimeout);
        }

        solve(captcha, waitOptions);
    }

    /**
     * Sends captcha to `/in.php` and waits for it's result.
     * This helper can be used instead of manual using of `send` and `getResult` functions.
     *
     * @param captcha  captcha
     * @param waitOptions  waitOptions
     * @throws Exception  Exception
     */
    public void solve(Captcha captcha, Map<String, Integer> waitOptions) throws Exception {
        captcha.setId(send(captcha));

        if (!lastCaptchaHasCallback) {
            waitForResult(captcha, waitOptions);
        }
    }

    /**
     * This helper waits for captcha result, and when result is ready, returns it
     *
     * @param captcha  captcha
     * @param waitOptions  waitOptions
     * @throws Exception  Exception
     */
    public void waitForResult(Captcha captcha, Map<String, Integer> waitOptions) throws Exception {
        long startedAt = (long)(System.currentTimeMillis() / 1000);

        int timeout = waitOptions.getOrDefault("timeout", this.defaultTimeout);
        int pollingInterval = waitOptions.getOrDefault("pollingInterval", this.pollingInterval);

        while (true) {
            long now = (long)(System.currentTimeMillis() / 1000);

            if (now - startedAt < timeout) {
                Thread.sleep(pollingInterval * 1000);
            } else {
                break;
            }

            try {
                String result = getResult(captcha.getId());
                if (result != null) {
                    captcha.setCode(result);
                    return;
                }
            } catch (NetworkException e) {
                // ignore network errors
            }
        }

        throw new TimeoutException("Timeout " + timeout + " seconds reached");
    }

    /**
     * Sends captcha to '/in.php', and returns its `id`
     *
     * @param captcha  captcha
     * @return  result
     * @throws Exception  Exception
     */
    public String send(Captcha captcha) throws Exception {
        Map<String, String> params = captcha.getParams();
        Map<String, File> files = captcha.getFiles();

        sendAttachDefaultParams(params);

        validateFiles(files);

        String response = apiClient.in(params, files);

        if (!response.startsWith("OK|")) {
            throw new ApiException("Cannot recognise api response (" + response + ")");
        }

        return response.substring(3);
    }

    /**
     * Returns result of captcha if it was solved or `null`, if result is not ready
     *
     * @param id  ID of captcha returned by in.php
     * @return  result
     * @throws Exception  Exception
     */
    public String getResult(String id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "get");
        params.put("id", id);

        String response = res(params);

        if (response.equals("CAPCHA_NOT_READY")) {
            return null;
        }

        if (!response.startsWith("OK|")) {
            throw new ApiException("Cannot recognise api response (" + response + ")");
        }

        return response.substring(3);
    }

    /**
     * Gets account's balance
     *
     * @return  result
     * @throws Exception  Exception
     */
    public double balance() throws Exception {
        String response = res("getbalance");
        return Double.parseDouble(response);
    }

    /**
     * Reports if captcha was solved correctly (sends `reportbad` or `reportgood` to `/res.php`)
     *
     * @param id  ID of captcha returned by in.php
     * @param correct  correct
     * @throws Exception  Exception
     */
    public void report(String id, boolean correct) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        if (correct) {
            params.put("action", "reportgood");
        } else {
            params.put("action", "reportbad");
        }

        res(params);
    }

    /**
     * Makes request to `/res.php`
     *
     * @param action action
     * @return  result
     * @throws Exception  Exception
     */
    private String res(String action) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", action);
        return res(params);
    }

    /**
     * Makes request to `/res.php`
     *
     * @param params  parameters
     * @return  result
     * @throws Exception  Exception
     */
    private String res(Map<String, String> params) throws Exception {
        params.put("key", apiKey);
        return apiClient.res(params);
    }

    /**
     * Attaches default parameters to request
     *
     * @param params  parameters
     */
    private void sendAttachDefaultParams(Map<String, String> params) {
        params.put("key", apiKey);

        if (callback != null) {
            if (!params.containsKey("pingback")) {
                params.put("pingback", callback);
            } else if (params.get("pingback").length() == 0) {
                params.remove("pingback");
            }
        }

        lastCaptchaHasCallback = params.containsKey("pingback");

        if (softId != 0 && !params.containsKey("soft_id")) {
            params.put("soft_id", String.valueOf(softId));
        }
    }

    /**
     * Validates if files parameters are correct
     *
     * @param files files
     * @throws ValidationException  ValidationException
     */
    private void validateFiles(Map<String, File> files) throws ValidationException {
        for (Map.Entry<String, File> entry : files.entrySet()) {
            File file = entry.getValue();

            if (!file.exists()) {
                throw new ValidationException("File not found: " + file.getAbsolutePath());
            }

            if (!file.isFile()) {
                throw new ValidationException("Resource is not a file: " + file.getAbsolutePath());
            }
        }
    }

}
