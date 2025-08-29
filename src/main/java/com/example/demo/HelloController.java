package com.example.demo;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class HelloController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/")
    public String home() {
        return "Spring Boot is running! ✅";
    }

    @GetMapping("/submit")
    public String submit() {
        try {
            // Step 1: Generate webhook
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> body = Map.of(
                    "name", "Shweta",
                    "regNo", "22BCE7567",
                    "email", "shwetarana9927@gmail.com"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

            String webhookUrl = (String) response.getBody().get("webhook");
            String token = (String) response.getBody().get("accessToken");

            // Step 2: Your final SQL query
            String finalQuery = "SELECT p.AMOUNT AS SALARY, " +
                    "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                    "FLOOR(DATEDIFF(CURDATE(), e.DOB) / 365) AS AGE, " +
                    "d.DEPARTMENT_NAME " +
                    "FROM PAYMENTS p " +
                    "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                    "AND p.AMOUNT = (SELECT MAX(AMOUNT) FROM PAYMENTS WHERE DAY(PAYMENT_TIME) <> 1)";

            // Step 3: Submit solution
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> queryBody = Map.of("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(queryBody, headers);

            ResponseEntity<String> submitResponse =
                    restTemplate.postForEntity(webhookUrl, request, String.class);

            return "Submission Response: " + submitResponse.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during submission: " + e.getMessage();
        }
    }
}
