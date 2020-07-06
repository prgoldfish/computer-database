package com.excilys.cdb.mapper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.model.Company;

public class CompanyMapperTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testToDTO() throws MapperException {
        Company c = new Company(1, "Company");
        CompanyDTO res = new CompanyDTO("1", "Company");
        assertEquals(res, CompanyMapper.toDTO(c));
    }

    @Test(expected = MapperException.class)
    public void testToDTONullParameter() throws MapperException {
        CompanyMapper.toDTO(null);
    }

    @Test
    public void testFromDTO() throws MapperException {
        Company res = new Company(1, "Company");
        CompanyDTO c = new CompanyDTO("1", "Company");
        assertEquals(res, CompanyMapper.fromDTO(c));
    }

    @Test(expected = MapperException.class)
    public void testFromDTONullParameter() throws MapperException {
        CompanyMapper.fromDTO(null);
    }

    @Test(expected = MapperException.class)
    public void testFromDTOInvalidID() throws MapperException {
        CompanyMapper.fromDTO(new CompanyDTO("invalidID", "Company"));
    }

}
