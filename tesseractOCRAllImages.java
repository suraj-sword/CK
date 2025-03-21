package PDFReader;
import java.io.File;
import java.io.FilenameFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.testng.annotations.Test;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class tesseractOCRAllImages {
    String imageFolderPath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf1"; 

    static {
        System.load("C:\\Users\\Suraj Mishra\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }

    @Test
    public void extractTextFromImages() {
        File folder = new File(imageFolderPath);

        File[] imageFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg");
            }
        });

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No image files found in the folder.");
            return;
        }

        // Process each image in the folder
        for (File imageFile : imageFiles) {
            String preprocessedImagePath = "C:\\Users\\Suraj Mishra\\eclipse-workspace\\PDF\\Pdf1\\preprocessed_" + imageFile.getName();
            preprocessImage(imageFile.getAbsolutePath(), preprocessedImagePath);
            extractTextFromImage(preprocessedImagePath);
        }
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
        Photo.fastNlMeansDenoising(highResGray, denoisedImage, 21, 7, 22);

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
            System.out.println("<<=====================================================================================================>>");
        } catch (TesseractException e) {
            System.err.println("Error processing " + ": " + e.getMessage());
            
                   }
    }
}
