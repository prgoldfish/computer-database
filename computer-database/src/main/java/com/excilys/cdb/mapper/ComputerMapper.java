package com.excilys.cdb.mapper;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;
import com.excilys.cdb.exception.ComputerMapperException;
import com.excilys.cdb.model.Computer;

public class ComputerMapper {
    
    public static ComputerDTO toDTO(Computer c) throws ComputerMapperException
    {
        if(c == null)
        {
            throw new ComputerMapperException("Computer object is null");
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
            dto.setEntreprise(c.getEntreprise().getNom());
        }        
        return dto.build();
    }

}
