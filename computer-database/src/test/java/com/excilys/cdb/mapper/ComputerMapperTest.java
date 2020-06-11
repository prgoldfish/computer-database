package com.excilys.cdb.mapper;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

public class ComputerMapperTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testToDTO() throws MapperException {
        ComputerBuilder cb = new Computer.ComputerBuilder(123, "PC")
                .setDateIntroduction(LocalDateTime.of(2002, 1, 1, 0, 0));
        cb.setDateDiscontinuation(LocalDateTime.of(2003, 1, 1, 0, 0));
        cb.setEntreprise(new Company(1, "Company"));
        Computer c = cb.build();
        ComputerBuilderDTO resBuilder = new ComputerDTO.ComputerBuilderDTO("123", "PC")
                .setDateIntroduction("2002-01-01").setDateDiscontinuation("2003-01-01");
        resBuilder.setEntreprise(new CompanyDTO("1", "Company"));
        ComputerDTO res = resBuilder.build();
        assertEquals(res, ComputerMapper.toDTO(c));
    }

    @Test
    public void testToDTONoOptionalParameter() throws MapperException {
        Computer c = new Computer.ComputerBuilder(123, "PC").build();
        ComputerDTO res = new ComputerDTO.ComputerBuilderDTO("123", "PC").build();
        assertEquals(res, ComputerMapper.toDTO(c));
    }

    @Test(expected = MapperException.class)
    public void testToDTONullParameter() throws MapperException {
        ComputerMapper.toDTO(null);
    }

    @Test
    public void testFromDTO() throws MapperException {
        ComputerBuilder resBuilder = new Computer.ComputerBuilder(123, "PC")
                .setDateIntroduction(LocalDateTime.of(2002, 1, 1, 0, 0));
        resBuilder.setDateDiscontinuation(LocalDateTime.of(2003, 1, 1, 0, 0));
        resBuilder.setEntreprise(new Company(1, "Company"));
        Computer res = resBuilder.build();
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("2002-01-01")
                .setDateDiscontinuation("2003-01-01");
        cb.setEntreprise(new CompanyDTO("1", "Company"));
        ComputerDTO c = cb.build();
        assertEquals(res, ComputerMapper.fromDTO(c));
    }

    @Test
    public void testFromDTONoOptionalParameter() throws MapperException {
        Computer res = new Computer.ComputerBuilder(123, "PC").build();
        ComputerDTO c = new ComputerDTO.ComputerBuilderDTO("123", "PC").build();
        assertEquals(res, ComputerMapper.fromDTO(c));
    }

    @Test(expected = MapperException.class)
    public void testFromDTONullParameter() throws MapperException {
        ComputerMapper.fromDTO(null);
    }

    @Test(expected = MapperException.class)
    public void testFromDTOBadID() throws MapperException {
        ComputerMapper.fromDTO(new ComputerDTO.ComputerBuilderDTO("unparsable", "aaa").build());
    }

    @Test(expected = MapperException.class)
    public void testFromDTOEmptyName() throws MapperException {
        ComputerMapper.fromDTO(new ComputerDTO.ComputerBuilderDTO("1", "").build());
    }

    @Test(expected = MapperException.class)
    public void testFromDTONullName() throws MapperException {
        ComputerMapper.fromDTO(new ComputerDTO.ComputerBuilderDTO("1", null).build());
    }

    @Test(expected = MapperException.class)
    public void testFromDTOInvalidCompany() throws MapperException {
        ComputerMapper.fromDTO(new ComputerDTO.ComputerBuilderDTO("1", "aaa")
                .setEntreprise(new CompanyDTO("unparsable", "aaa")).build());
    }

    @Test(expected = MapperException.class)
    public void testFromDTODiscontinuationButNoIntroduction() throws MapperException {
        ComputerMapper
                .fromDTO(new ComputerDTO.ComputerBuilderDTO("1", "aaa").setDateDiscontinuation("2002-01-01").build());
    }

    @Test
    public void testFromDTOIntroductionOnly() throws MapperException {
        ComputerBuilder resBuilder = new Computer.ComputerBuilder(123, "PC")
                .setDateIntroduction(LocalDateTime.of(2002, 1, 1, 0, 0));
        Computer res = resBuilder.build();
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("2002-01-01");
        ComputerDTO c = cb.build();
        assertEquals(res, ComputerMapper.fromDTO(c));
    }

    @Test(expected = MapperException.class)
    public void testFromDTOInvalidIntroductionString() throws MapperException {
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("invalid");
        ComputerDTO c = cb.build();
        ComputerMapper.fromDTO(c);
    }

    @Test(expected = MapperException.class)
    public void testFromDTOInvalidDiscontinuationString() throws MapperException {
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("2002-01-01")
                .setDateDiscontinuation("invalid");
        ComputerDTO c = cb.build();
        ComputerMapper.fromDTO(c);
    }

    @Test(expected = MapperException.class)
    public void testFromDTOIntroductionAfterDiscontinuation() throws MapperException {
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("2003-01-01")
                .setDateDiscontinuation("2002-01-01");
        ComputerDTO c = cb.build();
        ComputerMapper.fromDTO(c);
    }

    @Test
    public void testFromDTOEmptyDateStrings() throws MapperException {
        ComputerBuilder resBuilder = new Computer.ComputerBuilder(123, "PC");
        Computer res = resBuilder.build();
        ComputerBuilderDTO cb = new ComputerDTO.ComputerBuilderDTO("123", "PC").setDateIntroduction("")
                .setDateDiscontinuation("");
        ComputerDTO c = cb.build();
        assertEquals(res, ComputerMapper.fromDTO(c));
    }

}
