package com.increff.pos.dto;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.flow.InventoryFlowService;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService service;

    @Autowired
    private InventoryFlowService flowService;

    public void delete(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    public InventoryData get(@PathVariable int id) throws ApiException {
        InventoryPojo p = service.get(id);
        return convert(p);
    }

    public List<InventoryData> getAll() {
        List<InventoryPojo> list = service.getAll();
        List<InventoryData> list2 = new ArrayList<InventoryData>();
        for (InventoryPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    public void update(@PathVariable int id, @RequestBody InventoryForm f, String method) throws ApiException {
        InventoryPojo p = convert(f,method);
        service.update(id, p);
    }

    public void updateByBarcode(InventoryForm f) throws ApiException{
       InventoryPojo p = convert(f,"add");
       service.update(p.getId(),p);
    }

    public void validate(InventoryForm f) throws ApiException{
        emptyCheck(f);
        checkProductExists(f);
    }


    private  InventoryData convert(InventoryPojo p) {
        InventoryData d = new InventoryData();
        String barcode = flowService.getProductBarcodeById(p.getId());
        d.setBarcode(barcode);
        d.setQuantity(p.getQuantity());
        d.setId(p.getId());
        return d;
    }

    private void checkProductExists(InventoryForm f) throws ApiException {
        int id = flowService.getProductIdByBarcode(f.getBarcode());
        InventoryPojo invPojo = service.get(id);
        if(invPojo == null)
            throw new ApiException("Product barcode is not valid");
    }

    private  InventoryPojo convert(InventoryForm f,String method) throws ApiException {
        normalize(f);
        int id = flowService.getProductIdByBarcode(f.getBarcode());
        int quantity = 0;
        InventoryPojo invPojo = service.get(id);

        if(invPojo != null)
            quantity = invPojo.getQuantity();

        if(method == "add")
        quantity += f.getQuantity();

        invPojo.setQuantity(quantity);
        return invPojo;
    }


    public static void normalize(InventoryForm f){
        f.setBarcode(StringUtil.toLowerCase(f.getBarcode()));
    }

    public static void emptyCheck(InventoryForm f) throws ApiException{
        if(StringUtil.isEmpty(f.getBarcode()) || f.getQuantity() < 0)
            throw  new ApiException("Invalid data entered.");
    }

}
