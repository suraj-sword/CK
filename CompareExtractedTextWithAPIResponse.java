package PDFReader;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class CompareExtractedTextWithAPIResponse {
    WebDriver driver;
    String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65053558404800.png";
    String apiEndpoint = "https://be.qa1.cloudonomic.net/lens/database/getCostByOperatingSystem?service=EC2&reportGranularity=MONTHLY&month=11&year=2024&cudosCostType=UNBLENDED";

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    @Test
    public void extractTextAndCompareWithAPI() {
        String extractedText = extractTextFromImage(imagePath);
        System.out.println("Extracted Text from Image:\n" + extractedText);

        Map<String, Object> apiResponseData = fetchDataFromAPI();
        System.out.println("Fetched API Response: " + new GsonBuilder().setPrettyPrinting().create().toJson(apiResponseData));

        compareExtractedTextWithAPI(extractedText, apiResponseData);
    }

    private String extractTextFromImage(String imagePath) {
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            return tesseract.doOCR(new File(imagePath)).trim();
        } catch (TesseractException e) {
            throw new RuntimeException("Error while reading image: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> fetchDataFromAPI() {
        Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("auth-module", "1")
            .header("auth-mav", "2821")
            .header("Auth-Partner", "2000009")
            .header("Auth-Customer", "2000149")
            .header("Authorization", "Bearer c5wFeNuqCzETSKg+tXij")
            .when()
            .get(apiEndpoint);

        Assert.assertEquals(response.getStatusCode(), 200, "API response failed!");
        return response.jsonPath().getMap("");  // Return the whole response map
    }

    private void compareExtractedTextWithAPI(String extractedText, Map<String, Object> apiData) {
        // Extract the 'data' section from the API response
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) apiData.get("data");

        // Access the 'tableRows' section, which contains the rows of data
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tableRows = (List<Map<String, Object>>) data.get("tableRows");

        // Iterate through the rows to find the Sep-2024 data
        boolean matchFound = false;
        for (Map<String, Object> row : tableRows) {
            @SuppressWarnings("unchecked")
            Map<String, Object> columnValuesForRow = (Map<String, Object>) row.get("columnValuesForRow");

            // Check if 'Sep-2024' exists in the column values
            if (columnValuesForRow != null && columnValuesForRow.containsKey("Sep-2024")) {
            	
                @SuppressWarnings("unchecked")
				Map<String, Object> sep2024Data = (Map<String, Object>) columnValuesForRow.get("Sep-2024");

            // Extract the required fields

                
                String columnName = (String) sep2024Data.get("columnName");
                String rowName = (String) sep2024Data.get("rowName");
                Object cellValueObject = sep2024Data.get("cellValue");
                

             // Handle the case where cellValue can be either a Float or a Double
                Double cellValue = null;
                if (cellValueObject instanceof Float) {
                    cellValue = ((Float) cellValueObject).doubleValue();
                } else if (cellValueObject instanceof Double) {
                    cellValue = (Double) cellValueObject;
                }

                
                System.out.println("<<====================Extracted "+columnName+ " Data From API=================>>");
                System.out.println(rowName);
                System.out.println(cellValue);

          

               // Now, compare the extracted text with the data for Sep-2024
                String combinedText = cleanApiText(String.valueOf(cellValue)) + " " + 
                                      cleanApiText(String.valueOf(rowName));

               // Clean the extracted text
                String cleanedExtractedText = cleanText(extractedText);
                
               // Now compare the cleaned extracted text with the combined expected text
                if (cleanedExtractedText.contains(combinedText)) {
                    System.out.println("<<===========================================================================>>");

                    System.out.println("Success: Found Expected Text in PDF: " + combinedText);
                    matchFound = true;
                    break;  
                }

        }
        }

        if (!matchFound) {
            Assert.fail("Failed: No expected text found in extracted data for Sep-2024.");
        }
    }
    

    private String cleanText(String text) {
        return text.replaceAll("[$,@]", "").trim().toLowerCase();
    }

    private String cleanApiText(String text) {
        return text.replaceAll("[=,]", " ").trim().toLowerCase();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
