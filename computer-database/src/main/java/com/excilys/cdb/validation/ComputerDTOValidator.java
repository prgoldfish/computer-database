package com.excilys.cdb.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;

@Component
public class ComputerDTOValidator implements Validator {

    @Autowired
    CompanyDTOValidator companyDtoValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return ComputerBuilderDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ComputerBuilderDTO dto = (ComputerBuilderDTO) target;
        validateDates(dto.getDateIntroduction(), dto.getDateDiscontinuation(), errors);
        validateId(errors, dto.getId());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nom", "computerDTO.nom.empty");
        if (dto.getEntreprise() != null) {
            companyDtoValidator.validate(dto.getEntreprise(), errors);
        }

    }

    private void validateId(Errors errors, String id) {
        Long longId = null;
        try {
            longId = Long.parseLong(id);

        } catch (NumberFormatException nfe) {
            errors.rejectValue("id", "computerDTO.id.nan");
        }
        if (longId != null && longId <= 0) {
            errors.rejectValue("id", "computerDTO.id.negativeId");
        }
    }

    private void validateDates(String intro, String discont, Errors errors) {
        boolean introSet = intro != null && !intro.isBlank();
        boolean discontSet = discont != null && discont.isBlank();
        boolean bothSet = introSet && discontSet;
        LocalDate introDate = null;
        LocalDate discontDate = null;

        if (introSet) {
            try {
                introDate = LocalDate.parse(intro, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException dtpe) {
                bothSet = false;
                errors.rejectValue("dateIntroduction", "computerDTO.dateIntroduction.invalid"); //TODO: Add : The introduced date is invalid
            }
        }
        if (discontSet) {
            if (!introSet) {
                bothSet = false;
                errors.rejectValue("dateDiscontinuation", "computerDTO.dateDiscontinuation.introNotSet"); //TODO: Add : The discontinued date is set but the introduced date is not
            } else {
                try {
                    discontDate = LocalDate.parse(discont, DateTimeFormatter.ISO_DATE);
                } catch (DateTimeParseException dtpe) {
                    bothSet = false;
                    errors.rejectValue("dateDiscontinuation", "computerDTO.dateDiscontinuation.invalid"); //TODO: Add : The discontinued date is invalid
                }
            }
        }
        if (bothSet && introDate.isAfter(discontDate)) {
            errors.rejectValue("dateIntroduction", "computerDTO.dateIntroduction.afterDiscont"); //TODO: Add : The introduction date is after the discontinuation date
        }
    }

}
