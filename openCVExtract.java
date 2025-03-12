
package PDFReader;

import java.io.File;
import org.testng.annotations.Test;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class openCVExtract {
	
      //  String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65053558404800.png";	   
      // String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\320984435254100.png";
	  String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65055702114600.png";
	    
    
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

    }






