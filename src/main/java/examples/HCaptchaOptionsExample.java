package examples;

import com.azcaptcha.AZCaptcha;
import com.azcaptcha.captcha.HCaptcha;

public class HCaptchaOptionsExample {

    public static void main(String[] args) {
        AZCaptcha solver = new AZCaptcha("YOUR_API_KEY");

        HCaptcha captcha = new HCaptcha();
        captcha.setSiteKey("10000000-ffff-ffff-ffff-000000000001");
        captcha.setUrl("https://www.site.com/page/");
        captcha.setProxy("HTTPS", "login:password@IP_address:PORT");

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

}
