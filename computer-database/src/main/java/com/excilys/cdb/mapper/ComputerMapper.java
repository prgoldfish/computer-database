package com.excilys.cdb.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

public class ComputerMapper {
    
    public static ComputerDTO toDTO(Computer c) throws MapperException
    {
        if(c == null)
        {
            throw new MapperException("Computer object is null");
        }
        ComputerBuilderDTO dto = new ComputerDTO.ComputerBuilderDTO(Long.toString(c.getId()), c.getNom());
        if(c.getDateIntroduction() != null)
        {
            dto.setDateIntroduction(c.getDateIntroduction().toString());
        }
        if(c.getDateDiscontinuation() != null)
        {
            dto.setDateDiscontinuation(c.getDateDiscontinuation().toString());
        }
        if(c.getEntreprise() != null)
        {
            dto.setEntreprise(CompanyMapper.toDTO(c.getEntreprise()));
        }        
        return dto.build();
    }
    
    public static Computer fromDTO(ComputerDTO c) throws MapperException
    {
        List<String> errors = new ArrayList<>();
        if(c == null)
        {
            throw new MapperException("ComputerDTO object is null");
        }
        int id = 0;
        try
        {
            id = Integer.parseInt(c.getId());
        } catch (NumberFormatException nfe) {
            errors.add("The id cannot be translated to a number");
        }
        if(c.getNom() == null || c.getNom().trim().equals(""))
        {
            errors.add("The computer's name is missing");
        }
        ComputerBuilder result = new Computer.ComputerBuilder(id, c.getNom());
        parseDates(c, errors, result);
        if(c.getEntreprise() != null)
        {
            try
            {
                result.setEntreprise(CompanyMapper.fromDTO(c.getEntreprise()));
            } catch (MapperException mape) {
                errors.add(mape.getMessage());
            }
        }
        
        if(!errors.isEmpty())
        {
            throw new MapperException("Errors when parsing the computerDTO object", errors);
        }
        
        return result.build();
    }

    private static void parseDates(ComputerDTO c, List<String> errors, ComputerBuilder result) {
        boolean introSet = c.getDateIntroduction() != null && !c.getDateIntroduction().equals("");
        boolean discontSet = c.getDateDiscontinuation() != null && !c.getDateDiscontinuation().equals("");
        boolean bothSet = introSet && discontSet;
        if(introSet)
        {
            try {
                result.setDateIntroduction(LocalDate.parse(c.getDateIntroduction(), DateTimeFormatter.ISO_DATE).atStartOfDay());
            } catch (DateTimeParseException dtpe) {
                bothSet = false;
                errors.add("The introduced date is invalid");
            }
        }
        if(discontSet)
        {
            if(!introSet)
            {
                bothSet = false;
                errors.add("The discontinued date is set but the introduced date is not");
            }
            else 
            {
                try {
                    result.setDateDiscontinuation(LocalDate.parse(c.getDateDiscontinuation(), DateTimeFormatter.ISO_DATE).atStartOfDay());
                } catch (DateTimeParseException dtpe) {
                    bothSet = false;
                    errors.add("The discontinued date is invalid");
                }
            }                
        }
        if(bothSet && result.getDateIntroduction().isAfter(result.getDateDiscontinuation()))
        {
            errors.add("The introduction date is after the discontinuation date");
        }
    }

}
