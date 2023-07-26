package com.increff.pos.controller;

import com.increff.pos.util.IOUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ErrorDownloadController {

    @Value("${error.errorFileDirectory}")
    private String outputErrorFileDirectory;

    @RequestMapping(value = "/error/{fileName:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-disposition:", "attachment; filename=" + fileName);
        File f = new File(outputErrorFileDirectory+"/"+fileName);
        InputStream is = new FileInputStream(f);
        try {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtil.closeQuietly(is);
        }

    }

    @GetMapping("/error/exists/{fileName:.+}")
    public ResponseEntity<Object> checkIfFileExists(@PathVariable("fileName") String fileName) {
        File file = new File(outputErrorFileDirectory+"/"+fileName);
        if (file.exists()) {
            return ResponseEntity.ok().body("File exists.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
