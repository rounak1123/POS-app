package com.increff.pos.util;

import com.increff.pos.service.ApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorUtil<T> {
    public  static  <T> Set<ConstraintViolation<T>> validateForm(T obj){
         ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         Validator validator = factory.getValidator();
         return validator.validate(obj);
    }


    public  static <T> void validateFormCheck(T obj) throws ApiException {

        Set<ConstraintViolation<T>> violations = validateForm(obj);
        List<String> errorList;
        String errorString = "Error while validating form: \n";
        if(!violations.isEmpty()) {
            errorList = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());

            for(String error : errorList){
                errorString += error + "\n";
            }
            throw new ApiException(errorString);
        }

    }
}
