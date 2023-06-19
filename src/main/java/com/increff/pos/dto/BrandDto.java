package com.increff.pos.dto;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class BrandDto {

    @Autowired
    private BrandService service;


    public void add(@RequestBody BrandForm form) throws ApiException {
        BrandPojo p = convert(form);
        service.add(p);
    }
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    public BrandData get(@PathVariable int id) throws ApiException {
        BrandPojo p = service.get(id);
        return convert(p);
    }

    public List<BrandData> getAll() {
        List<BrandPojo> list = service.getAll();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
        BrandPojo p = convert(f);
        service.update(id, p);
    }

    private  BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setCategory(p.getCategory());
        d.setBrand(p.getBrand());
        d.setId(p.getId());
        return d;
    }

    private  BrandPojo convert(BrandForm f) {
        normalize(f);
        BrandPojo p = new BrandPojo();
        p.setBrand(f.getBrand());
        p.setCategory(f.getCategory());
        return p;
    }

    public static void normalize(BrandForm f){
        f.setBrand(StringUtil.toLowerCase(f.getBrand()));
        f.setCategory(StringUtil.toLowerCase(f.getCategory()));
    }



}
