
package PDFReader;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ApiResponseTrim {
    WebDriver driver;
    String pdfFilePath = "C:/Users/Suraj Mishra/Downloads/1MG-ALL (2).pdf"; 
    String apiEndpoint = "https://be.qa1.cloudonomic.net/lens/cost-explorer/getCostExplorerChartTable"; 

    @BeforeTest
    public void setup() {
        driver = new EdgeDriver();
        
    }

    @Test
    public void ApiAmountComparisionwithPDFExtractedData() throws IOException {
        // Extract text from PDF
        File file = new File(pdfFilePath);
        if (!file.exists()) {
            throw new IOException("PDF file not found at: " + pdfFilePath);
        }

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bf = new BufferedInputStream(fis);
        PDDocument pdfdoc = PDDocument.load(bf); 

        PDFTextStripper pdfStr = new PDFTextStripper();
        String pdfText = pdfStr.getText(pdfdoc);

    pdfdoc.close();

        System.out.println("Extracted PDF Text:\n" + pdfText);

        

        // Fetch data from API
        Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("auth-module", "1")
            .header("auth-mav", "2821")
            .header("Auth-Partner", "2000009")
            .header("Authorization", "Bearer ZWyqh6CdC3uygiOmbfZ7wdriAkwbyMehM3P9EJRtUNRrziu")
            .body("{\"startDate\":1722450600000,\"endDate\":1730399399999,\"groupBy\":\"SERVICE\",\"period\":\"MONTHLY\",\"filterDetailMap\":{}}")
            .when()
            .post(apiEndpoint);
        
       // System.out.println("API Response:\n " + response.asString());
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Fetched API Response: " + gson.toJson(response.jsonPath().getMap("")));

        Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");

     // Extract data from API
        List<String> expectedTexts = new ArrayList<>();

        try {            
            // Extract values for multiple months and add to list
            expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.costDates.'Aug-2024'"));
            
            expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.costDates.'Sep-2024'"));
            
            expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.costDates.'Oct-2024'"));
            
            expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.totalCost"));

            if (expectedTexts.isEmpty()) {
                throw new RuntimeException("API response does not contain expected data.");
            }
            
            System.out.println("Extracted Data from API: " + expectedTexts);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting API response data. Full response: " + response.asString(), e);
        }

        // Compare PDF data with API data
        for (Object expected : expectedTexts) {  
            String expectedText = cleanApiText(expected.toString());  
            String cleanedPdfText = cleanText(pdfText);
            
            if (cleanedPdfText.contains(expectedText)) {
                System.out.println(" Success: Found expected amount " + expectedText + " in PDF.");
            } else {
                Assert.fail(" Failed: Missing text in PDF: " + expectedText);
            }
        }
    }

        // Function to clean extracted and expected text
        public static String cleanText(String text) {
            return text.replaceAll("[$,]", "").trim().toLowerCase(); 
        }

        public static String cleanApiText(String text) {
            return text.replaceAll("[=,]", " ").trim().toLowerCase();
        }

        @AfterTest
        public void tearDown() {
            if (driver != null) {
                driver.quit();
            }
        }
}



