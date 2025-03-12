package PDFReader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.cos.COSName;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;

public class SaveExtractedImageTextbyOCR {

    // Extract text from PDF
    public static String extractTextFromPDF(String pdfFilePath) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfFilePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    // Extract images from PDF
    public static void extractImagesFromPDF(String pdfFilePath, String outputFolder) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfFilePath));
        PDPageTree pages = document.getPages();

        int pageNumber = 0;
        for (PDPage page : pages) {
            pageNumber++;
            PDResources resources = page.getResources();

            // Get all image XObjects from the page
            for (COSName xObjectName : resources.getXObjectNames()) {
                if (resources.isImageXObject(xObjectName)) {
                    PDImageXObject imageXObject = (PDImageXObject) resources.getXObject(xObjectName);
                    BufferedImage bImage = imageXObject.getImage();

                    // calculate the image size
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "PNG", baos);
                    baos.flush();
                    byte[] imageData = baos.toByteArray();
                    baos.close();

                    // Check the file size
                    long fileSizeInBytes = imageData.length;
                    long fileSizeInKB = fileSizeInBytes / 1024; // file size in KB

                    // Only save the image if the size is between 30 KB and 150 KB
                    if (fileSizeInKB >= 30 && fileSizeInKB <= 150) {
                        // Save the image
                        File outputDir = new File(outputFolder);
                        if (!outputDir.exists()) {
                            outputDir.mkdirs();
                        }

                        File outputFile = new File(outputDir, "image_" + pageNumber + "_" + xObjectName.getName() + ".png");
                        ImageIO.write(bImage, "PNG", outputFile);
                        System.out.println("Saved image: " + outputFile.getAbsolutePath());
                    }
                }
            }
        }

        document.close();
    }

    // PdfTask for parallel execution
    public static class PdfTask implements Callable<Void> {
        private String pdfFilePath;

        public PdfTask(String pdfFilePath) {
            this.pdfFilePath = pdfFilePath;
        }

        @Override
        public Void call() {
            try {
                // Extract text from PDF
                String text = extractTextFromPDF(pdfFilePath);

                // Print extracted PDF text
                System.out.println("<<==============================================================================================================================>>");
                System.out.println("         Extracted Content from PDF File: ====>> " + new File(pdfFilePath).getName());
                System.out.println("<<==============================================================================================================================>>");
                System.out.println(text);
                System.out.println("<<==============================================================================================================================>>");

                // Extract images and process OCR
                String outputFolder = "C:/Users/Suraj Mishra/eclipse-workspace/PDF/Extracted_Images/" + new File(pdfFilePath).getName().replace(".pdf", "");
                extractImagesFromPDF(pdfFilePath, outputFolder);

                // Process OCR on extracted images
                processOCROnExtractedImages(outputFolder);

            } catch (IOException e) {
                System.err.println("Error processing PDF file: " + pdfFilePath);
                e.printStackTrace();
            }
            return null;
        }

        private void processOCROnExtractedImages(String imageFolderPath) {
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
                // Print saved image path
                System.out.println("Saved image: " + imageFile.getAbsolutePath());

                // Extract text from image and print
                extractTextFromImage(imageFile.getAbsolutePath());
            }
        }

        public static void extractTextFromImage(String imagePath) {
            try {
                ITesseract tesseract = new Tesseract();
                tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
                tesseract.setLanguage("eng");
                tesseract.setPageSegMode(3); 
                tesseract.setOcrEngineMode(1);
                tesseract.setTessVariable("user_defined_dpi", "300");

                // Extract text from the image
                File imageFile = new File(imagePath);
                String extractedText = tesseract.doOCR(imageFile);

                // Print extracted text
                System.out.println(extractedText);
                System.out.println("<<==============================================================================================================================>>");

            } catch (TesseractException e) {
                System.err.println("Error processing image: " + imagePath + ": " + e.getMessage());
            }
        }
    }

    @Test
    public void extractPDFsInParallel() {
        String folderPath = "C:/Users/Suraj Mishra/eclipse-workspace/PDF/PDFFiles/";

        File folder = new File(folderPath);
        File[] pdfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            System.out.println("No PDF files found in the specified folder.");
            return;
        }
        
        
        ExecutorService executorService = Executors.newFixedThreadPool(pdfFiles.length);

        for (File pdfFile : pdfFiles) {
            executorService.submit(new PdfTask(pdfFile.getAbsolutePath()));
        }

        for (File pdfFile : pdfFiles) {
            Future<Void> pdfTaskFuture = executorService.submit(new PdfTask(pdfFile.getAbsolutePath()));
            try {
                pdfTaskFuture.get(); 

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }
       
}
