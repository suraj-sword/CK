package PDFReader;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;


public class PDFReader {

    // Method to extract images from the entire PDF document
    public List<RenderedImage> getImagesFromPdf(PDDocument document) throws IOException {
        List<RenderedImage> images = new ArrayList<>();
        
        // Iterate over each page and extract images from its resources
        for (PDPage page : document.getPages()) {
            images.addAll(getImagesFromResources(page.getResources()));
        }
        
        return images;
    }

    // Extract images from the resources of a given page
    public List<RenderedImage> getImagesFromResources(PDResources resources) throws IOException {
        List<RenderedImage> images = new ArrayList<>();
        
        // Iterate over XObjects in the resources
        for (COSName xObjectName : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(xObjectName);
            
            // If the XObject is a form XObject, extract its images recursively
            if (xObject instanceof PDFormXObject) {
                images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
            }
            // If the XObject is an image XObject, add it to the list
            else if (xObject instanceof PDImageXObject) {
                images.add(((PDImageXObject) xObject).getImage());
            }
        }
        
        return images;
    }
    
    
    // Extract only relevant images from PDF (if the size is between 30 KB and 150 KB)

    public void PDFBoxExtractImages(PDDocument document) throws Exception {
        PDPageTree list = document.getPages();
        for (PDPage page : list) {
            PDResources pdResources = page.getResources();
            for (COSName c : pdResources.getXObjectNames()) {
                PDXObject o = pdResources.getXObject(c);
                if (o instanceof PDImageXObject) {
                    PDImageXObject pdImageXObject = (PDImageXObject) o;
                    BufferedImage image = pdImageXObject.getImage();

                    // Save the image to a temporary file to check its size
                    File tempFile = new File("./Pdf1/" + System.nanoTime() + ".png");
                    ImageIO.write(image, "png", tempFile);

                    // Check the file size (in bytes)
                    long fileSize = tempFile.length(); // file size in bytes

                    // Convert the file size to KB
                    long fileSizeInKB = fileSize / 1024; // file size in KB

                    // Only save the image if the size is between 30 KB and 150 KB
                    if (fileSizeInKB >= 30 && fileSizeInKB <= 150) {
                    	
                        // Move the file to the correct location if needed
                        System.out.println("Image saved: " + tempFile.getAbsolutePath());
                    } else {
                        // Delete the temporary file if the size is not in the desired range
                        tempFile.delete();
                    }
                }
            }
        }
    }
     
    
   /* // Extract images all images from PDF
    public void PDFBoxExtractImages (PDDocument document) throws Exception {
    	PDPageTree list = document.getPages();
    	for (PDPage page : list) {
    		PDResources pdResources = page.getResources();
    		for (COSName c : pdResources.getXObjectNames()) {
    			PDXObject o = pdResources.getXObject(c);
    			if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
    				File file = new File ("./Pdf/" + System.nanoTime() + ".png");
    				ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage(), "png", file);
    			}
    		}
    	}
    }*/
}
