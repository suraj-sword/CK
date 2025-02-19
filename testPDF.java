package PDFReader;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class testPDF {
	
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


//Assert.assertTrue(pdftext.contains("Account Wise Cost Summary"));
//Assert.assertTrue(pdftext.contains("AWS Service Wise Cost Comparison (Last 3 months)"));
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

@Test

public void ExtractPdfImages() throws Exception {
	
	URL pdfUrl = new URI("file:///C:/Users/Suraj%20Mishra/Downloads/1MG-ALL%20(2).pdf").toURL();
	URLConnection urlConnection = pdfUrl.openConnection();
	urlConnection.addRequestProperty("User-Agent", "Edge");
	InputStream ip= pdfUrl.openStream();
	BufferedInputStream bf = new BufferedInputStream(ip);
	
	PDDocument pdfdoc= PDDocument.load(bf);
	
	PDFReader reader = new PDFReader();
	reader.getImagesFromPdf(pdfdoc).size();
	reader.PDFBoxExtractImages(pdfdoc);
	
}

@AfterTest
public void tearDown () {
WebDriver driver = new EdgeDriver();
driver.close();
	
}
}
