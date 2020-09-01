package examples;

import com.azcaptcha.AZCaptcha;

import java.io.File;
import com.azcaptcha.captcha.Normal;

public class NormalOptionsExample {

    public static void main(String[] args) {
        AZCaptcha solver = new AZCaptcha("YOUR_API_KEY");

        Normal captcha = new Normal();
        captcha.setFile("src/main/resources/normal_2.jpg");
        captcha.setHintImg(new File("src/main/resources/normal_hint.jpg"));
        captcha.setHintText("Type red symbols only");

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

}
