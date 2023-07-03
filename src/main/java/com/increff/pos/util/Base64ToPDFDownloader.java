package com.increff.pos.util;
import java.io.*;
import java.util.Base64;
import javax.servlet.http.HttpServletResponse;

public class Base64ToPDFDownloader {
    public static void downloadBase64AsPDF(HttpServletResponse response, String base64String, String fileName) {
        try {
            // Decode the Base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);

            // Set response headers for PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Write the decoded bytes to the response output stream
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(decodedBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error generating PDF download: " + e.getMessage());
        }
    }
}