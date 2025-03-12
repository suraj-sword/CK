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

public class AWSServiceWiseCostComparison {
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
            .header("Authorization", "Bearer M/EAseXJ3ZRX2Gxez8Sseto7j7rB0tdbWcT/iTUUrvGkGmpou50vVGN+Lk1OGnj1")
            .body("{\"startDate\":1722450600000,\"endDate\":1730399399999,\"groupBy\":\"SERVICE\",\"period\":\"MONTHLY\",\"filterDetailMap\":{}}")
            .when()
            .post(apiEndpoint);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Fetched API Response: " + gson.toJson(response.jsonPath().getMap("")));

        Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");

        // Extract data from API
        List<String> expectedTexts = new ArrayList<>();

        try {            
            // function to extract, convert and add to expectedTexts
            addServiceDataToExpectedTexts(response, "Amazon Elastic Compute Cloud", expectedTexts);
            addServiceDataToExpectedTexts(response, "Amazon Relational Database Service", expectedTexts);
            addServiceDataToExpectedTexts(response, "Savings Plans for AWS Compute usage", expectedTexts);
            addServiceDataToExpectedTexts(response, "AWS Data Transfer", expectedTexts);
            addServiceDataToExpectedTexts(response, "Amazon Simple Storage Service", expectedTexts);
            addServiceDataToExpectedTexts(response, "Amazon ElastiCache", expectedTexts);
            addServiceDataToExpectedTexts(response, "AWS Marketplace", expectedTexts);
            addServiceDataToExpectedTexts(response, "Elastic Load Balancing", expectedTexts);
            addServiceDataToExpectedTexts(response, "AWS Database Migration Service", expectedTexts);
            addServiceDataToExpectedTexts(response, "CK Discounts", expectedTexts);
            addServiceDataToExpectedTexts(response, "Total", expectedTexts);

            

            if (expectedTexts.isEmpty()) {
                throw new RuntimeException("API response does not contain expected data.");
            }

            System.out.println("Extracted Data from API: " + expectedTexts);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting API response data. Full response: " + response.asString(), e);
        }

        // Lists to store matched and unmatched data
        List<String> matchedData = new ArrayList<>();
        List<String> unmatchedData = new ArrayList<>();

        // Compare PDF data with API data 
        for (Object expected : expectedTexts) {  
            String expectedText = cleanApiText(expected.toString());  
            String cleanedPdfText = cleanText(pdfText);
           
            String[] words = expectedText.split("\\s+"); // Splitting on spaces
            boolean allWordsFound = true;
            List<String> unmatchedWords = new ArrayList<>();

            for (String word : words) {
                if (!cleanedPdfText.contains(word)) {
                    unmatchedWords.add(word);  // Save unmatched word
                    allWordsFound = false;
                }
            }

            // Highlight missing words
            if (allWordsFound) {
                matchedData.add("Matched Data: " + expectedText);
            } else {
                String unmatchedText = "Unmatched Data: " + expectedText;
                if (!unmatchedWords.isEmpty()) {
                    unmatchedText += " [{*Following data did not matched in PDF: " + String.join(", ", unmatchedWords)+"*}]";
                }
                unmatchedData.add(unmatchedText);
            }
        }

        // Print all matched and unmatched data
        if (!matchedData.isEmpty()) {
            System.out.println("API Extracted data Successfully Matched with PDF Data:");
            System.out.println("=====================================================================================================================================================");
            matchedData.forEach(System.out::println);
        }

        if (!unmatchedData.isEmpty()) {
            System.out.println("\nAPI Extracted data Did Not Matched with PDF Data:");
            System.out.println("=====================================================================================================================================================");

            unmatchedData.forEach(System.out::println);
        } else {
            System.out.println("\nAll data matched successfully!");
        }

        // Fail the test if there are unmatched words
        if (!unmatchedData.isEmpty()) {
            Assert.fail("Failed: Some words are missing in PDF.");
        }
    }

    // Helper function to extract, convert and add to expectedTexts
    public void addServiceDataToExpectedTexts(Response response, String serviceName, List<String> expectedTexts) {
        List<Object> groupValues = response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == '" + serviceName + "' }.groupValue");
        List<Object> costDates = response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == '" + serviceName + "' }.costDates.");
        List<Object> totalCost = response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == '" + serviceName + "' }.totalCost.");

        // Create a string with groupValue, costDates, and totalCost in one line
        for (int i = 0; i < groupValues.size(); i++) {
            String combinedData = groupValues.get(i).toString() + " " +
                                  costDates.get(i).toString() + " " +
                                  totalCost.get(i).toString();
            expectedTexts.add(combinedData);
        }
    }

    // Clean extracted and expected text
    public static String cleanText(String text) {
        return text.replaceAll("[${},]", "").trim(); 
    }

    public static String cleanApiText(String text) {
        return text.replaceAll("[=,{}]", " ").trim();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
