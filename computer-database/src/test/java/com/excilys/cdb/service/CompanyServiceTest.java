package com.excilys.cdb.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.persistence.CompanyDAO;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

    @Mock
    private CompanyDAO dao;

    @InjectMocks
    private CompanyService companyService;
    private List<Company> compList;

    private void refreshMock() {
        refreshMockGetCompanyList();
        refreshMockGetCompanyByName();
    }

    private void refreshMockGetCompanyByName() {
        when(dao.getCompanyByName(anyString())).thenReturn(Optional.empty());
        compList.stream()
                .forEach(company -> when(dao.getCompanyByName(company.getNom())).thenReturn(Optional.of(company)));
    }

    private void refreshMockGetCompanyList() {
        when(dao.getCompaniesList()).thenReturn(compList);
    }

    @Before
    public void setUp() throws Exception {
        compList = new ArrayList<>();
        compList.add(new Company(1, "Company 1"));
        compList.add(new Company(2, "Company 2"));
        MockitoAnnotations.initMocks(this);
        refreshMock();
    }

    @Test
    public void testGetComputerList() throws ComputerServiceException {
        List<Company> resList = companyService.getCompaniesList();
        assertEquals(compList, resList);
    }

    @Test
    public void testGetComputerByName() {
        Optional<Company> c1 = companyService.getCompanyByName("Company 1");
        Optional<Company> c2 = companyService.getCompanyByName("Company 2");
        assert (c1.isPresent());
        assert (c2.isPresent());
        assertEquals(compList.get(0), c1.get());
        assertEquals(compList.get(1), c2.get());

    }

    @Test
    public void testGetComputerByNameNoResult() {
        Optional<Company> c1 = companyService.getCompanyByName("");
        Optional<Company> c2 = companyService.getCompanyByName("MégaCorporation");
        assert (c1.isEmpty());
        assert (c2.isEmpty());
    }

}
