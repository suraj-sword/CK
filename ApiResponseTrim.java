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
            .header("Authorization", "Bearer RGTTLtboDszauXmLHOrnE")
            .body("{\"startDate\":1722450600000,\"endDate\":1730399399999,\"groupBy\":\"SERVICE\",\"period\":\"MONTHLY\",\"filterDetailMap\":{}}")
            .when()
            .post(apiEndpoint);
                
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Fetched API Response: " + gson.toJson(response.jsonPath().getMap("")));

        Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");

     // Extract data from API
        List<String> expectedTexts = new ArrayList<>();

        try {            
          expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.costDates."));
           
        	
        	//expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.collect ['groupValue': it.groupValue, 'totalCost': it.totalCost, 'costDates': it.costDates"));

         // expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.collect { it.groupValue, it.totalCost, it.costDates }"));

        	
        //	expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.costDates."));
            
           // expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.totalCost"));
          //  expectedTexts.addAll(response.jsonPath().getList("data.costExplorerTableData.serviceSummary.totalCost"));

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
           
            String[] words = expectedText.split("\\s+"); // Splitting on spaces
 
            
           boolean allWordsFound = true;
  
            for (String word : words) {
                if (!cleanedPdfText.contains(word)) {
                    System.out.println("Warning: Missing word in PDF: " + word);
                    allWordsFound = false;
                }
            }

            if (allWordsFound) {
                System.out.println("Success: Found expected words in PDF." +expectedText);
            } else {
                Assert.fail("Failed: Some words are missing in PDF." );
            }
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



