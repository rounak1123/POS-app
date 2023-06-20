package com.increff.pos.dto;

import com.increff.pos.model.*;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.flow.ProductFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto {
    @Autowired
    private ProductService service;
    @Autowired
    private ProductFlowService flowService;

    public void add(@RequestBody ProductForm form) throws ApiException {
        normalize(form);
        emptyCheck(form);
        ProductPojo p = convert(form);
        service.add(p);
    }

    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    public ProductData get(@PathVariable int id) throws ApiException {
        ProductPojo p = service.get(id);
        return convert(p);
    }

    public List<ProductData> getAll() {
        List<ProductPojo> list = service.getAll();
        List<ProductData> list2 = new ArrayList<ProductData>();
        for (ProductPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
        ProductPojo p = convert(f);
        service.update(id, p);
    }

    public void validate(@RequestBody ProductForm f) throws ApiException {
        normalize(f);
        emptyCheck(f);
        ProductPojo p = convert(f);
        service.validate(p);
    }

    public ProductData convert(ProductPojo p) {
        ProductData d = new ProductData();
        String brand = flowService.getBrand(p.getBrand_category_id());
        String category = flowService.getCategory(p.getBrand_category_id());
        d.setBarcode(p.getBarcode());
        d.setBrand(brand);
        d.setCategory(category);
        d.setId(p.getId());
        d.setMrp(p.getMrp());
        d.setName(p.getName());

        return d;
    }

    public ProductPojo convert(ProductForm f) {
        normalize(f);
        ProductPojo p = new ProductPojo();
        int brandCategoryId = flowService.getBrandCategoryId(f.getBrand(), f.getCategory());
        p.setBarcode(f.getBarcode());
        p.setBrand_category_id(brandCategoryId);
        p.setName(f.getName());
        p.setMrp(f.getMrp());

        return p;
    }

    public void normalize(ProductForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()));
        f.setName(StringUtil.toLowerCase(f.getName()));
        f.setBrand(StringUtil.toLowerCase(f.getBrand()));
        f.setCategory(StringUtil.toLowerCase(f.getCategory()));
    }

    public void emptyCheck(ProductForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || StringUtil.isEmpty(f.getName()) ||
           StringUtil.isEmpty(f.getBrand()) || StringUtil.isEmpty(f.getCategory()) || f.getMrp() <= 0)
            throw new ApiException("Invalid or missing fields");
    }
}
