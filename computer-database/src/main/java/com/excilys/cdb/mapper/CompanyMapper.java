package com.excilys.cdb.mapper;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.model.Company;

public class CompanyMapper {

    public static CompanyDTO toDTO(Company c) throws MapperException
    {
        if(c == null)
        {
            throw new MapperException("Company object is null");
        }
        return new CompanyDTO(Long.toString(c.getId()), c.getNom());
    }

}
