package PDFReader;
import java.io.File;
import java.io.FilenameFilter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class tesseractOCRAllImages {
    WebDriver driver;
    String imageFolderPath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf"; // Change to your folder path

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    @Test
    public void extractTextFromImages() {
        File folder = new File(imageFolderPath);
        
        // Filter to select only image files (PNG, JPG, JPEG)
        File[] imageFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg");
            }
        });

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No image files found in the folder.");
            return;
        }

        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Set Tesseract OCR data path
        // tesseract.setLanguage("eng"); // Optional: Set language

        for (File imageFile : imageFiles) {
            try {
                System.out.println("\nProcessing Image: " + imageFile.getName());
                String extractedText = tesseract.doOCR(imageFile);
                System.out.println("Extracted Text:\n" + extractedText);
                System.out.println("--------------------------------------------------");
            } catch (TesseractException e) {
                System.err.println("Error processing " + imageFile.getName() + ": " + e.getMessage());
            }
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}



