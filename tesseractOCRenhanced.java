package PDFReader;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;  
import java.io.File;

public class tesseractOCRenhanced {

    static {
        System.load("C:\\Users\\Suraj Mishra\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }

    public static void main(String[] args) {
      //String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\320982751192300.png";
      //String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\250081169142000.png";

  	  //String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65055702114600.png";    
	 String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\65053558404800.png";
      
      
      
   //   String imagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf1\\774123563184900.png";


        String preprocessedImagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf\\preprocessed.png";
        preprocessImage(imagePath, preprocessedImagePath);
        extractTextFromImage(preprocessedImagePath);
    }

    public static void preprocessImage(String inputImagePath, String outputImagePath) {
        // Read image
        Mat image = Imgcodecs.imread(inputImagePath);
        if (image.empty()) {
            System.out.println("Could not open or find the image!");
            return;
        }
        
        Mat highResImage = new Mat();
        Imgproc.resize(image, highResImage, new Size(image.width() * 3.5, image.height() * 3.5), 0, 0, Imgproc.INTER_CUBIC);

        Mat highResGray = new Mat();
        Imgproc.cvtColor(highResImage, highResGray, Imgproc.COLOR_BGR2GRAY);

        Mat denoisedImage = new Mat();
        Photo.fastNlMeansDenoising(highResGray, denoisedImage, 14, 7, 22);

        Mat binaryImage = new Mat();
        Imgproc.adaptiveThreshold(denoisedImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        Core.bitwise_not(binaryImage, binaryImage);

        Imgcodecs.imwrite(outputImagePath, binaryImage);
        System.out.println("Image preprocessing done. Saved at: " + outputImagePath);
    }

    public static void extractTextFromImage(String imagePath) {
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(3); 
            tesseract.setOcrEngineMode(1);
            tesseract.setTessVariable("user_defined_dpi", "300");

            // Extract text
            File imageFile = new File(imagePath);
            String extractedText = tesseract.doOCR(imageFile);

            // Print extracted text
            System.out.println("Extracted Text from Image:\n" + extractedText);
        } catch (TesseractException e) {
            System.err.println("Error while reading image: " + e.getMessage());
        }
    }
}
