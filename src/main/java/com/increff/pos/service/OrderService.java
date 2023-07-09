package com.increff.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.model.InvoiceData;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.flow.OrderItemFlowService;
import com.increff.pos.util.Base64ToPDFDownloader;
import com.increff.pos.util.StringUtil;
import jdk.nashorn.internal.objects.NativeJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static com.mysql.cj.MysqlType.JSON;

@Service
public class OrderService {

    @Autowired
    private OrderDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo add(OrderPojo p) throws ApiException {
        if(dao.select(p.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
       return dao.insert(p);
    }

    @Transactional
    public void delete(int id)  throws ApiException{
        getCheck(id);
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void update(int id, OrderPojo p) throws ApiException {
        OrderPojo ex = getCheck(id);
        ex.setTime(p.getTime());
        ex.setStatus(p.getStatus());
        dao.update(ex);
    }

    @Transactional
    public OrderPojo getCheck(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return p;
    }

    // DOWNLOAD INVOICE METHODS

    @Transactional
    public ResponseEntity<Resource> downloadInvoice(InvoiceData invoiceData) throws IOException {

        String convertedBase64 =  getBase64String(invoiceData);

        byte[] pdfData = Base64.getDecoder().decode(convertedBase64);

        String filePath = "/Users/rounakagrawal/Desktop/POS/POS_Application/output.pdf";
        File outputFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(pdfData);
        fos.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", outputFile.getName());

        ResponseEntity<Resource> responseOut = new ResponseEntity<>(
                new FileSystemResource(outputFile),
                headers,
                HttpStatus.OK
        );
              return responseOut;

    }

    public String getBase64String(InvoiceData invoice) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:6969/api/generate-invoice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(invoice);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);


        String responseBody = responseEntity.getBody();
        return responseBody;
    }

}
