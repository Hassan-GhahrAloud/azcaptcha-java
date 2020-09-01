package examples;

import com.azcaptcha.AZCaptcha;
import com.azcaptcha.captcha.Normal;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class NormalBase64Example {

    public static void main(String[] args) throws Exception {
        AZCaptcha solver = new AZCaptcha("YOUR_API_KEY");

        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/normal.jpg"));
        String base64EncodedImage = Base64.getEncoder().encodeToString(bytes);

        Normal captcha = new Normal();
        captcha.setBase64(base64EncodedImage);

        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

}
