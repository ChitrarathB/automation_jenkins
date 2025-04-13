package utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Utility class to generate PDF reports with screenshots from test runs
 * Uses PDFBox directly to create PDF reports with embedded screenshots
 */
public class ScreenshotReportGenerator {

    // Font constants
    private static final PDFont TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont NORMAL_FONT = PDType1Font.HELVETICA;
    private static final float TITLE_FONT_SIZE = 18;
    private static final float HEADER_FONT_SIZE = 14;
    private static final float NORMAL_FONT_SIZE = 11;
    private static final float MARGIN = 50;
    
    // Main method for standalone execution
    public static void main(String[] args) {
        System.out.println("\n\n==== GENERATING SCREENSHOT PDF REPORT ====\n");
        
        try {
            // Create timestamp for unique file name
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            Path outputDir = Paths.get("target", "test-reports");
            Files.createDirectories(outputDir);
            
            // Generate a PDF report
            String pdfPath = outputDir + "/TestReport-" + timestamp + ".pdf";
            
            // List of image files to include in the PDF
            List<File> screenshotFiles = findScreenshots();
            
            if (screenshotFiles.isEmpty()) {
                System.out.println("No screenshots found to include in the report.");
            } else {
                System.out.println("Found " + screenshotFiles.size() + " screenshots to include in the report.");
                
                // Create the PDF report
                createPdfReport(pdfPath, screenshotFiles);
                
                System.out.println("PDF Report with Screenshots generated at: " + new File(pdfPath).getAbsolutePath());
            }
            
            System.out.println("\n==== REPORT GENERATION COMPLETE ====\n");
            
        } catch (Exception e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a PDF report with screenshots
     */
    public static void createPdfReport(String outputPath, List<File> imageFiles) throws IOException {
        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            
            // Add a title page
            addTitlePage(document);
            
            // Add each screenshot to its own page
            for (int i = 0; i < imageFiles.size(); i++) {
                File imageFile = imageFiles.get(i);
                
                try {
                    // Add a page for this screenshot
                    addImagePage(document, imageFile, i + 1);
                } catch (Exception e) {
                    System.err.println("Error adding image to PDF: " + e.getMessage());
                }
            }
            
            // Save the PDF
            document.save(outputPath);
        }
    }
    
    /**
     * Adds a title page to the PDF document
     */
    private static void addTitlePage(PDDocument document) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Title
            contentStream.beginText();
            contentStream.setFont(TITLE_FONT, TITLE_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);
            contentStream.showText("Test Execution Report");
            contentStream.endText();
            
            // Date and time
            contentStream.beginText();
            contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN - 30);
            contentStream.showText("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentStream.endText();
            
            // Description
            contentStream.beginText();
            contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN - 70);
            contentStream.showText("This report contains screenshots captured during test execution.");
            contentStream.endText();
            
            // Environment info
            contentStream.beginText();
            contentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN - 110);
            contentStream.showText("Test Environment");
            contentStream.endText();
            
            // Environment details
            float yPosition = page.getMediaBox().getHeight() - MARGIN - 140;
            
            contentStream.beginText();
            contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("OS: Linux (Replit)");
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("Browser: HtmlUnit (Headless)");
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.beginText();
            contentStream.setFont(NORMAL_FONT, NORMAL_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("Framework: Cucumber with Selenium WebDriver");
            contentStream.endText();
        }
    }
    
    /**
     * Adds a page with a screenshot to the PDF document
     */
    private static void addImagePage(PDDocument document, File imageFile, int pageNumber) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Image name as header
            contentStream.beginText();
            contentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);
            contentStream.showText("Screenshot " + pageNumber + ": " + imageFile.getName());
            contentStream.endText();
            
            // Add the image
            PDImageXObject image = PDImageXObject.createFromFileByContent(imageFile, document);
            
            // Calculate dimensions to fit in the page while maintaining aspect ratio
            float pageWidth = page.getMediaBox().getWidth() - 2 * MARGIN;
            float pageHeight = page.getMediaBox().getHeight() - 2 * MARGIN - 30; // Leave space for header
            
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();
            
            // Scale to fit width
            float scale = pageWidth / imageWidth;
            if (imageHeight * scale > pageHeight) {
                // If too tall, scale to fit height
                scale = pageHeight / imageHeight;
            }
            
            float scaledWidth = imageWidth * scale;
            float scaledHeight = imageHeight * scale;
            
            // Center the image horizontally
            float x = (page.getMediaBox().getWidth() - scaledWidth) / 2;
            float y = page.getMediaBox().getHeight() - MARGIN - 30 - scaledHeight; // Position below header
            
            contentStream.drawImage(image, x, y, scaledWidth, scaledHeight);
        }
    }
    
    /**
     * Finds screenshot files to include in the report
     */
    private static List<File> findScreenshots() {
        List<File> screenshots = new ArrayList<>();
        
        try {
            // First look in the most obvious location
            File[] extentReportBase = new File("target").listFiles(
                file -> file.isDirectory() && file.getName().startsWith("extent-reports")
            );
            
            if (extentReportBase != null && extentReportBase.length > 0) {
                // Sort by last modified to get the most recent
                Arrays.sort(extentReportBase, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                
                // Check base64 images in the most recent directory
                File base64Dir = new File(extentReportBase[0], "target/screenshots");
                if (base64Dir.exists() && base64Dir.isDirectory()) {
                    File[] files = base64Dir.listFiles((dir, name) -> 
                        name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                    );
                    
                    if (files != null) {
                        screenshots.addAll(Arrays.asList(files));
                        System.out.println("Found images in: " + base64Dir.getAbsolutePath());
                    }
                }
            }
            
            // If no screenshots found, check alternative locations
            if (screenshots.isEmpty()) {
                File directScreenshots = new File("target/screenshots");
                if (directScreenshots.exists() && directScreenshots.isDirectory()) {
                    File[] files = directScreenshots.listFiles((dir, name) -> 
                        name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                    );
                    
                    if (files != null) {
                        screenshots.addAll(Arrays.asList(files));
                        System.out.println("Found images in: " + directScreenshots.getAbsolutePath());
                    }
                }
            }
            
            // Another possible location
            if (screenshots.isEmpty()) {
                File[] dirs = new File("target").listFiles(File::isDirectory);
                if (dirs != null) {
                    for (File dir : dirs) {
                        if (dir.getName().contains("screenshot") || dir.getName().contains("extent")) {
                            File[] files = dir.listFiles((d, name) -> 
                                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                            );
                            
                            if (files != null && files.length > 0) {
                                screenshots.addAll(Arrays.asList(files));
                                System.out.println("Found images in: " + dir.getAbsolutePath());
                                break;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error finding screenshots: " + e.getMessage());
        }
        
        return screenshots;
    }
}