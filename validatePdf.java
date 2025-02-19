package PDFReader;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class validatePdf {
	
WebDriver driver;
@BeforeTest
public void setup (){
	driver = new EdgeDriver();
		driver.get("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf");
		
}
@Test
public void pdfReaderTest() throws IOException, URISyntaxException {

URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
InputStream ip= pdfUrl.openStream();
BufferedInputStream bf = new BufferedInputStream(ip);
	
// Page Count	
PDDocument pdfdoc= PDDocument.load(bf);
int pageCount= pdfdoc.getNumberOfPages();
System.out.println("Total count of the pages, this Pdf is having : " +pageCount);
Assert.assertEquals(pageCount, 23);


System.out.println("=========================== PDF Content ================");

//Page Content Text
PDFTextStripper pdfStr = new PDFTextStripper();
String pdftext= pdfStr.getText(pdfdoc);
System.out.println(pdftext);

Assert.assertTrue(pdftext.contains("Account Wise Cost - Elastic Compute Cloud"));
Assert.assertTrue(pdftext.contains("EC2-Running Hours - Cost By Operating System"));
Assert.assertTrue(pdftext.contains("EC2-Running Hours - Cost by Purchase Options and AWS"));
Assert.assertTrue(pdftext.contains("EC2-Others - Cost By API Operations"));
Assert.assertTrue(pdftext.contains("EC2-Others - Cost By AWS Accounts"));

}

@Test

public void AccountWiseCostSummary() throws IOException, URISyntaxException {
	
	URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
	InputStream ip= pdfUrl.openStream();
	BufferedInputStream bf = new BufferedInputStream(ip);
	
	PDDocument pdfdoc= PDDocument.load(bf);	
	PDFTextStripper pdfStr = new PDFTextStripper();
	String pdftext= pdfStr.getText(pdfdoc);

	Assert.assertTrue(pdftext.contains("Account Wise Cost Summary"));	
	 
}

@Test

public void AWSServiceWiseCostComparison() throws IOException, URISyntaxException {
	
	URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
	InputStream ip= pdfUrl.openStream();
	BufferedInputStream bf = new BufferedInputStream(ip);
	
	PDDocument pdfdoc= PDDocument.load(bf);
	
	PDFTextStripper pdfStr = new PDFTextStripper();
	String pdftext= pdfStr.getText(pdfdoc);

	Assert.assertTrue(pdftext.contains("AWS Service Wise Cost Comparison (Last 3 months)"));	
	
	  // Regex pattern to match Amazon Elastic Compute Cloud row
  Pattern pattern = Pattern.compile("Amazon Elastic Compute Cloud\\s*\\$([\\d,]+\\.\\d{2})\\s*\\$([\\d,]+\\.\\d{2})\\s*\\$([\\d,]+\\.\\d{2})\\s*\\$([\\d,]+\\.\\d{2})");

  Matcher matcher = pattern.matcher(pdftext);
    
    // Validate extracted amounts
   if (matcher.find()) {
	   
       System.out.println("Match Found: " + matcher.group(0));

       double augAmount = Double.parseDouble(matcher.group(1).replace(",", ""));
       double sepAmount = Double.parseDouble(matcher.group(2).replace(",", ""));
       double octAmount = Double.parseDouble(matcher.group(3).replace(",", ""));
       double totalAmount = Double.parseDouble(matcher.group(4).replace(",", ""));
        
       double calculatedTotal = augAmount + sepAmount + octAmount;
      
    	  Assert.assertEquals(calculatedTotal, totalAmount, 0.01);
          System.out.println("====== Test Passed: Both the total amounts match.=======");
          System.out.println("Total amount in PDF : " +totalAmount);
          System.out.println("Total Calculated Amount by adding all three : " +calculatedTotal);          
     
   }
    else {
    	
   Assert.fail("Amazon Elastic Compute Cloud data not found in PDF");
   }
   }

@Test

public void getSpecificPageText() throws IOException, URISyntaxException {
	
	URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
	InputStream ip= pdfUrl.openStream();
	BufferedInputStream bf = new BufferedInputStream(ip);
	
	PDDocument pdfdoc= PDDocument.load(bf);
	
	PDFTextStripper pdfStr = new PDFTextStripper();	
	// Get specific page content
	pdfStr.setStartPage(23);
	String pdftext1= pdfStr.getText(pdfdoc);
	System.out.println(pdftext1);
	
	Assert.assertTrue(pdftext1.contains("support@cloudkeeper.com"));
	
}

@Test

public void testPdfImagesCount() throws Exception {
	
	URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
	URLConnection urlConnection = pdfUrl.openConnection();
	urlConnection.addRequestProperty("User-Agent", "Edge");
	InputStream ip= pdfUrl.openStream();
	BufferedInputStream bf = new BufferedInputStream(ip);
	
	PDDocument pdfdoc= PDDocument.load(bf);
	
	PDFReader reader = new PDFReader();
	int imageCount= reader.getImagesFromPdf(pdfdoc).size();
	System.out.println("Total Images in this pdf = " +imageCount);
	
}

@AfterTest
public void tearDown () {
WebDriver driver = new EdgeDriver();
driver.quit();
	
}
}

