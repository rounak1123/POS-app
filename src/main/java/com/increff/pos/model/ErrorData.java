package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.HashMap;

@Component
@Scope(value= WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
@Getter @Setter
public class ErrorData implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<Integer, String> errorList;
    private boolean hasErrorOnUpload;

    public ErrorData() {
        errorList = new HashMap<>();
        hasErrorOnUpload = false;
    }

    public void addErrorMessage(int rowNumber, String Error) {

        String prevError = errorList.get(rowNumber);
        if(prevError == null){
            errorList.put(rowNumber, Error);
        }else {
            String newError = prevError + ", " +Error;
            errorList.put(rowNumber, newError);
        }

    }

}
