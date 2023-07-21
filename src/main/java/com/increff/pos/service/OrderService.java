package com.increff.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.dao.OrderDao;
import com.increff.pos.model.InvoiceData;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Value("${invoiceApp.baseUrl}")
    private String invoiceAppUrl;

    public OrderPojo add(OrderPojo orderPojo) throws ApiException {
        if(orderDao.select(orderPojo.getId()) != null)
            throw new ApiException("The Order Item already exists in the table.");
       return orderDao.insert(orderPojo);
    }

    public void delete(int id)  throws ApiException{
        getCheck(id);
        orderDao.delete(id);
    }
    public OrderPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public List<OrderPojo> getAll() {
        return orderDao.selectAll();
    }

    public void update(int id, OrderPojo p) throws ApiException {
        OrderPojo ex = getCheck(id);
        ex.setTime(p.getTime());
        ex.setStatus(p.getStatus());
        orderDao.update(ex);
    }

    private OrderPojo getCheck(int id) throws ApiException {
        OrderPojo orderPojo = orderDao.select(id);
        if (orderPojo == null) {
            throw new ApiException("OrderItem with given ID does not exit, id: " + id);
        }
        return orderPojo;
    }

    // DOWNLOAD INVOICE METHODS

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

    private String getBase64String(InvoiceData invoiceData) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(invoiceAppUrl);
        String url = invoiceAppUrl + "/api/generate-invoice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(invoiceData);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);


        String responseBody = responseEntity.getBody();
        return responseBody;
    }

}
