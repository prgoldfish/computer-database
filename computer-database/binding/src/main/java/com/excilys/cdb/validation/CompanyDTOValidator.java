package com.excilys.cdb.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excilys.cdb.dto.CompanyDTO;

@Component
public class CompanyDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CompanyDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CompanyDTO dto = (CompanyDTO) target;
        validateId(errors, dto.getId());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nom", "companyDTO.nom.empty");
    }

    private void validateId(Errors errors, String id) {
        Long longId = null;
        try {
            longId = Long.parseLong(id);

        } catch (NumberFormatException nfe) {
            errors.rejectValue("id", "CompanyDTO.id.nan");
        }
        if (longId != null && longId <= 0) {
            errors.rejectValue("id", "CompanyDTO.id.negativeId");
        }
    }

}
