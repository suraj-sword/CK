package PDFReader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiplePDFParallelly {

    public static String extractTextFromPDF(String pdfFilePath) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfFilePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }
 
    public static class PdfTask implements Callable<String> {
        private String pdfFilePath;

        public PdfTask(String pdfFilePath) {
            this.pdfFilePath = pdfFilePath;
        }

        @Override
        public String call() throws Exception {
            return extractTextFromPDF(pdfFilePath);
        }
    }

    @Test
    public void extractPDFsInParallel() {
        // Define the folder path
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
            Future<String> pdfContentFuture = executorService.submit(new PdfTask(pdfFile.getAbsolutePath()));
            try {
                String pdfText = pdfContentFuture.get(); 
                
                System.out.println("         Extracted Content from PDF File: ====>> " + pdfFile.getName());
                System.out.println("<<=================================================================================================================================>>");
                System.out.println("                                                                                                                                     ");
                System.out.println(pdfText);
                System.out.println("<<=================================================================================================================================>>");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       executorService.shutdown();
    }
}
