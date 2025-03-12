package PDFReader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.cos.COSName;
import org.testng.annotations.Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.ByteArrayOutputStream;

public class SaveExtractedImagesFromPDFs {

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
        public Void call() throws Exception {
            // Extract text
            String text = extractTextFromPDF(pdfFilePath);

            System.out.println("                                                                                                                                          ");
            System.out.println("<<======================================================================================================================================>>");
            System.out.println("         Extracted Content from PDF File: ====>> " + new File(pdfFilePath).getName());
            System.out.println("<<======================================================================================================================================>>");
            System.out.println("                                                                                                                                          ");
            System.out.println(text);
            System.out.println("<<======================================================================================================================================>>");

            // Extract images
            String outputFolder = "C:/Users/Suraj Mishra/eclipse-workspace/PDF/Extracted_Images/" + new File(pdfFilePath).getName().replace(".pdf", "");
            extractImagesFromPDF(pdfFilePath, outputFolder);

            return null;
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
