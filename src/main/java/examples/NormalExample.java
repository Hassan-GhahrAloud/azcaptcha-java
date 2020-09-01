package examples;

import com.azcaptcha.AZCaptcha;
import com.azcaptcha.captcha.Normal;

public class NormalExample {

    public static void main(String[] args) {
        AZCaptcha solver = new AZCaptcha("YOUR_API_KEY");

        Normal captcha = new Normal("src/main/resources/normal.jpg");

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

}
