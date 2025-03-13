package PDFReader;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class CompareImageExtractedDataWithAPIResponsectOCR {
    WebDriver driver;
    String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65053558404800.png";
    String apiEndpoint = "https://be.qa1.cloudonomic.net/lens/database/getCostByOperatingSystem?service=EC2&reportGranularity=MONTHLY&month=09&year=2024&cudosCostType=UNBLENDED"; 

    
    // Replace this with your API URL
    String apiUrl = "https://example.com/api/endpoint"; 

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    @Test
    public void extractTextFromImage() {
        try {
            // Initialize Tesseract OCR
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

            // Read the image file and extract text
            File imageFile = new File(imagePath);
            String extractedText = tesseract.doOCR(imageFile);

            // Print extracted text
            System.out.println("Extracted Text from Image:\n" + extractedText);

            // Now compare the extracted text with the API response
            String apiResponse = getApiResponse();
            compareText(extractedText, apiResponse);

        } catch (TesseractException e) {
            System.err.println("Error while reading image: " + e.getMessage());
        }
    }

 // Fetch data from API
    

    Response response = RestAssured.given()
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
    .header("auth-module", "1")
    .header("auth-mav", "2821")
    .header("Auth-Partner", "2000009")
    .header("Auth-Customer", "2000149")
    .header("Authorization", "Bearer M/+Lk1OGnj1")
    .when()
    .get(apiEndpoint);
    
    

    
    
    
    
     gson = new GsonBuilder().setPrettyPrinting().create();
     
     
     
    System.out.println("Fetched API Response: " + gson.toJson(response.jsonPath().getMap("")));

    Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");
    
    
    
    
    
    // Method to get the response from the API
    private String getApiResponse() {
        StringBuilder response = new StringBuilder();
        try {
            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Use GET or POST depending on the API
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching API response: " + e.getMessage());
        }
        return response.toString();
    }

    // Method to compare extracted text with API response
    private void compareText(String extractedText, String apiResponse) {
        // Simple comparison, you can enhance it as needed
        if (extractedText.trim().equalsIgnoreCase(apiResponse.trim())) {
            System.out.println("The extracted text matches the API response.");
        } else {
            System.out.println("The extracted text does not match the API response.");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

