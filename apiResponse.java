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

public class apiResponse {
    WebDriver driver;
    String pdfFilePath = "C:/Users/Suraj Mishra/Downloads/1MG-ALL (2).pdf"; 
    String apiEndpoint = "https://be.qa1.cloudonomic.net/lens/cost-explorer/getCostExplorerChartTable"; 

    @BeforeTest
    public void setup() {
        driver = new EdgeDriver();
        
    }

    @Test
    public void servicesNameComparisionWithPDFExtractedData() throws IOException {
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
            .header("Authorization", "Bearer ZWyqh6CdC3uygi4Kc5okp/JJ+GYCoRiOmbfZ7wdriAkwbyMehM3P9EJRtUNRrziu")
            .body("{\"startDate\":1722450600000,\"endDate\":1730399399999,\"groupBy\":\"SERVICE\",\"period\":\"MONTHLY\",\"filterDetailMap\":{}}")
            .when()
            .post(apiEndpoint);
        
        System.out.println("API Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");
       
        //Extract data from API
        List<String> expectedTexts = new ArrayList<>();;		
      
       try {            
         //  expectedTexts = response.jsonPath().getList("data.costExplorerTableData.serviceSummary.findAll { it.groupValue == 'Amazon Elastic Compute Cloud' }.costDates.Aug-2024");
           expectedTexts = response.jsonPath().getList("data.costExplorerTableData.serviceSummary.groupValue");

            if (expectedTexts == null || expectedTexts.isEmpty()) {
                throw new RuntimeException("API response does not contain expected data.");
            }
            
            System.out.println("Extracted Data from API: " + expectedTexts);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting API response data. Full response: " + response.asString(), e);
        }
//Compare PDF data with API data
       for (Object expected : expectedTexts) {  
    	   
    	    String expectedText = expected.toString(); 
    	    
    	    Assert.assertTrue(pdfText.toLowerCase().contains(expectedText.toLowerCase()), "Missing text in PDF: " + expectedText);

    	}
    }

    @AfterTest
   
    
   public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}



