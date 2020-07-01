package com.excilys.cdb.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;

@Component
public class ComputerDTOValidator implements Validator {

    @Autowired
    CompanyDTOValidator companyDtoValidator;

    @Autowired
    MessageSource messageSources;

    private List<String> errorList = new ArrayList<>();

    public List<String> getErrorList() {
        return errorList;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ComputerBuilderDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ComputerBuilderDTO dto = (ComputerBuilderDTO) target;
        validateDates(dto.getDateIntroduction(), dto.getDateDiscontinuation(), errors);
        validateId(errors, dto.getId());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nom", "empty");
        if (dto.getEntreprise() != null) {
            companyDtoValidator.validate(dto.getEntreprise(), errors);
        }
        addErrors(errors);

    }

    private void validateId(Errors errors, String id) {
        Long longId = null;
        try {
            longId = Long.parseLong(id);

        } catch (NumberFormatException nfe) {
            errors.rejectValue("id", "nan");
        }
        if (longId != null && longId <= 0) {
            errors.rejectValue("id", "negativeId");
        }
    }

    private void validateDates(String intro, String discont, Errors errors) {
        boolean introSet = intro != null && !intro.isBlank();
        boolean discontSet = discont != null && !discont.isBlank();
        boolean bothSet = introSet && discontSet;
        LocalDate introDate = null;
        LocalDate discontDate = null;

        if (introSet) {
            try {
                introDate = LocalDate.parse(intro, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException dtpe) {
                bothSet = false;
                errors.rejectValue("dateIntroduction", "invalid");
            }
        }
        if (discontSet) {
            if (!introSet) {
                bothSet = false;
                errors.rejectValue("dateDiscontinuation", "introNotSet");
            } else {
                try {
                    discontDate = LocalDate.parse(discont, DateTimeFormatter.ISO_DATE);
                } catch (DateTimeParseException dtpe) {
                    bothSet = false;
                    errors.rejectValue("dateDiscontinuation", "invalid");
                }
            }
        }
        if (bothSet && introDate.isAfter(discontDate)) {
            errors.rejectValue("dateIntroduction", "afterDiscont");
        }
    }

    private void addErrors(Errors errors) {
        List<ObjectError> errorObjList = errors.getAllErrors();
        for (ObjectError errObj : errorObjList) {
            String[] codes = errObj.getCodes();
            for (String msgCode : codes) {
                try {
                    String message = messageSources.getMessage(msgCode, null, Locale.ROOT);
                    errorList.add(message);
                    //System.out.println("Message found : " + message);
                    break;
                } catch (NoSuchMessageException nsme) {
                }
            }
        }

    }

}
