package PDFReader;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class tesseractOCR {
    WebDriver driver;
   String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\320978477033300.png";
  //  String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\320981742055400.png";

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
          //  tesseract.setLanguage("eng");

            // Read the image file and extract text
            File imageFile = new File(imagePath);
            String extractedText = tesseract.doOCR(imageFile);

            // Print extracted text
            System.out.println("Extracted Text from Image:\n" + extractedText);
        } catch (TesseractException e) {
            System.err.println("Error while reading image: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


