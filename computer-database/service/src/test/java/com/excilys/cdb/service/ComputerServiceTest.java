package com.excilys.cdb.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.persistence.OrderByColumn;

@RunWith(MockitoJUnitRunner.class)
public class ComputerServiceTest {

    @Mock
    private ComputerDAO dao;

    @InjectMocks
    private ComputerService computerService;

    private List<Computer> compList;

    private void refreshMock() {
        refreshMockGetComputerList();
        refreshMockGetMaxId();
        refreshMockGetComputerById();
        refreshMockGetComputerByName();
        refreshMockGetAddComputer();
        refreshMockUpdateComputer();
        refreshMockDeleteComputer();
        refreshSearchComputerByName();
    }

    private void refreshMockGetMaxId() {
        when(dao.getMaxId()).thenReturn((long) compList.size());
    }

    private void refreshMockDeleteComputer() {
        doAnswer(invoc -> {
            long id = invoc.getArgument(0, Long.class);
            compList = compList.stream().filter(c -> c.getId() != id).collect(Collectors.toList());
            return "Done";
        }).when(dao).deleteComputer(anyLong());
    }

    private void refreshMockUpdateComputer() {
        doAnswer(invoc -> {
            Computer argCom = invoc.getArgument(0, Computer.class);
            compList = compList.stream().map(com -> com.getId() == argCom.getId() ? argCom : com)
                    .collect(Collectors.toList());
            return "Done";
        }).when(dao).updateComputer(any(Computer.class));
    }

    private void refreshMockGetAddComputer() {
        doAnswer(invoc -> compList.add(invoc.getArgument(0))).when(dao).addComputer(any(Computer.class));
    }

    private void refreshMockGetComputerByName() {
        when(dao.getComputerByName(anyString())).thenReturn(Optional.empty());
        compList.stream()
                .forEach(computer -> when(dao.getComputerByName(computer.getName())).thenReturn(Optional.of(computer)));
    }

    private void refreshMockGetComputerById() {
        when(dao.getComputerById(longThat(i -> i <= 0 || i >= compList.size()))).thenReturn(Optional.empty());
        for (int j = 0; j < compList.size(); j++) {
            when(dao.getComputerById(j + 1)).thenReturn(Optional.of(compList.get(j)));
        }
    }

    private void refreshMockGetComputerList() {
        when(dao.getComputerList(anyLong(), anyLong(), any(OrderByColumn.class), anyBoolean())).thenAnswer(invoc -> {
            int startIndex = invoc.getArgument(0, Long.class).intValue();
            int limit = invoc.getArgument(1, Long.class).intValue();
            OrderByColumn order = invoc.getArgument(2, OrderByColumn.class);
            boolean ascendent = invoc.getArgument(3, Boolean.class);
            if (startIndex >= 0 && startIndex < compList.size() && limit > 0) {
                if (startIndex + limit < compList.size()) {
                    return orderCompList(compList, order, ascendent).subList(startIndex, startIndex + limit);
                } else {
                    return orderCompList(compList, order, ascendent).subList(startIndex, compList.size());
                }
            }
            return new ArrayList<>();
        });
    }

    private void refreshSearchComputerByName() {
        when(dao.searchComputersByName(anyString(), any(OrderByColumn.class), anyBoolean())).thenAnswer(invoc -> {
            String searchString = invoc.getArgument(0, String.class);
            OrderByColumn order = invoc.getArgument(1, OrderByColumn.class);
            boolean ascendent = invoc.getArgument(2, Boolean.class);
            return orderCompList(compList, order, ascendent).stream().filter(c -> c.getName().contains(searchString))
                    .collect(Collectors.toList());
        });
    }

    private List<Computer> orderCompList(List<Computer> list, OrderByColumn order, boolean ascendent) {
        if (order == null) {
            return list;
        }
        List<Computer> newList = new ArrayList<Computer>(list);
        newList.sort((Computer c1, Computer c2) -> compareComputer(c1, c2, order, ascendent));
        return newList;
    }

    private int compareComputer(Computer c1, Computer c2, OrderByColumn order, boolean ascendent) {
        if (order == null) {
            return (int) (c1.getId() - c2.getId());
        }
        int retValue = 0;
        switch (order) {
        case COMPANYID:
            long comp1 = c1.getCompany() == null ? 0 : c1.getCompany().getId();
            long comp2 = c2.getCompany() == null ? 0 : c2.getCompany().getId();
            retValue = (int) (comp1 - comp2);
        case COMPANYNAME:
            String compName1 = c1.getCompany() == null ? "" : c1.getCompany().getName();
            String compName2 = c2.getCompany() == null ? "" : c2.getCompany().getName();
            retValue = compName1.compareTo(compName2);
        case COMPUTERDISCONT:
            LocalDateTime dateDiscont1 = c1.getDiscontinued() == null ? LocalDateTime.MIN : c1.getDiscontinued();
            LocalDateTime dateDiscont2 = c2.getDiscontinued() == null ? LocalDateTime.MIN : c2.getDiscontinued();
            retValue = dateDiscont1.compareTo(dateDiscont2);
        case COMPUTERINTRO:
            LocalDateTime dateIntro1 = c1.getIntroduced() == null ? LocalDateTime.MIN : c1.getIntroduced();
            LocalDateTime dateIntro2 = c2.getIntroduced() == null ? LocalDateTime.MIN : c2.getIntroduced();
            retValue = dateIntro1.compareTo(dateIntro2);
        case COMPUTERNAME:
            retValue = c1.getName().compareTo(c2.getName());
        default:
            retValue = Long.compare(c1.getId(), c2.getId());
        }
        if (!ascendent) {
            retValue = -retValue;
        }
        return retValue;
    }

    @Before
    public void setUp() throws Exception {
        compList = new ArrayList<>();
        compList.add(new Computer.ComputerBuilder(1, "PC 1").setIntroduced(LocalDateTime.now()).build());
        compList.add(new Computer.ComputerBuilder(2, "PC 2").setIntroduced(LocalDateTime.now()).build());
        refreshMock();
    }

    @Test
    public void testGetComputerList() throws ComputerServiceException {
        List<Computer> resList = computerService.getComputerList(0, 20, OrderByColumn.COMPUTERID, true);
        List<Computer> resList2 = computerService.getComputerList(1, 20, OrderByColumn.COMPUTERID, true);
        List<Computer> resList3 = computerService.getComputerList(0, 1, OrderByColumn.COMPUTERID, true);
        assertEquals(compList, resList);
        assertEquals(compList.subList(1, compList.size()), resList2);
        assertEquals(compList.subList(0, 1), resList3);

    }

    @Test
    public void testGetComputerListBadStartIndex() {
        try {
            computerService.getComputerList(-12, 20, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
        try {

            computerService.getComputerList(compList.size() + 10, 20, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testGetComputerBadLimit() {
        try {
            computerService.getComputerList(0, -123, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testGetComputerById() {
        Optional<Computer> c1 = computerService.getComputerById(1);
        Optional<Computer> c2 = computerService.getComputerById(2);
        assert (c1.isPresent());
        assert (c2.isPresent());
        assertEquals(compList.get(0), c1.get());
        assertEquals(compList.get(1), c2.get());

    }

    @Test
    public void testGetComputerByIdNoResult() {
        Optional<Computer> c1 = computerService.getComputerById(78);
        Optional<Computer> c2 = computerService.getComputerById(-100);
        assert (c1.isEmpty());
        assert (c2.isEmpty());
    }

    @Test
    public void testGetComputerByName() {
        Optional<Computer> c1 = computerService.getComputerByName("PC 1");
        Optional<Computer> c2 = computerService.getComputerByName("PC 2");
        assert (c1.isPresent());
        assert (c2.isPresent());
        assertEquals(compList.get(0), c1.get());
        assertEquals(compList.get(1), c2.get());

    }

    @Test
    public void testGetComputerByNameNoResult() {
        Optional<Computer> c1 = computerService.getComputerByName("");
        Optional<Computer> c2 = computerService.getComputerByName("Supercalculateur");
        assert (c1.isEmpty());
        assert (c2.isEmpty());
    }

    @Test
    public void testAddNewComputer() throws ComputerServiceException {
        computerService.buildNewComputer("PC de Test");
        computerService.addComputerToDB();
        refreshMock();
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        assertEquals(toCompare, computerService.getComputerByName("PC de Test").get());
    }

    @Test
    public void testAddNewComputerNoBuild() {
        try {
            computerService.addComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testAddNewComputerFromAnother() throws ComputerServiceException {
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        computerService.buildComputerForUpdate(toCompare);
        try {
            computerService.addComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testUpdateComputer() throws ComputerServiceException {
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        computerService.buildComputerForUpdate(toCompare);
        computerService.addIntroDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        computerService.updateComputerToDB();
        refreshMock();
        Computer toCompareWithIntro = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test")
                .setIntroduced(LocalDateTime.of(2020, 1, 1, 0, 0)).build();
        assertEquals(toCompareWithIntro, computerService.getComputerByName("PC de Test").get());
    }

    @Test
    public void testUpdateComputerNoBuild() {
        try {
            computerService.updateComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            //			e.printStackTrace();
        }
    }

    @Test
    public void testUpdateComputerFromNew() throws ComputerServiceException {
        computerService.buildNewComputer("PC de Test");
        try {
            computerService.updateComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            //          e.printStackTrace();
        }
    }

    @Test
    public void testDeleteComputer() throws ComputerServiceException {
        computerService.deleteComputer(1);
        assertEquals(compList, computerService.getComputerList(0, 1000, OrderByColumn.COMPUTERID, true));
    }

    @Test
    public void testDeleteComputerNotExisting() {
        try {
            computerService.deleteComputer(1234);
            assert (false);
        } catch (ComputerServiceException e) {
            //            e.printStackTrace();
        }
    }

    @Test
    public void testSearchComputerByName() {
        assertEquals(compList, computerService.searchComputersByName("PC", OrderByColumn.COMPUTERID, true));
    }

    @Test
    public void testSearchComputerByNameNoResult() {
        assertEquals(Collections.EMPTY_LIST,
                computerService.searchComputersByName("AZERTY", OrderByColumn.COMPUTERID, true));
    }

}
