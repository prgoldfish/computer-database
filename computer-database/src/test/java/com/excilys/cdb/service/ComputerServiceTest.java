package com.excilys.cdb.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.persistence.OrderByColumn;

public class ComputerServiceTest {

    private ComputerDAO dao;
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
                .forEach(computer -> when(dao.getComputerByName(computer.getNom())).thenReturn(Optional.of(computer)));
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
            return orderCompList(compList, order, ascendent).stream().filter(c -> c.getNom().contains(searchString))
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
            long comp1 = c1.getEntreprise() == null ? 0 : c1.getEntreprise().getId();
            long comp2 = c2.getEntreprise() == null ? 0 : c2.getEntreprise().getId();
            retValue = (int) (comp1 - comp2);
        case COMPANYNAME:
            String compName1 = c1.getEntreprise() == null ? "" : c1.getEntreprise().getNom();
            String compName2 = c2.getEntreprise() == null ? "" : c2.getEntreprise().getNom();
            retValue = compName1.compareTo(compName2);
        case COMPUTERDISCONT:
            LocalDateTime dateDiscont1 = c1.getDateDiscontinuation() == null ? LocalDateTime.MIN
                    : c1.getDateDiscontinuation();
            LocalDateTime dateDiscont2 = c2.getDateDiscontinuation() == null ? LocalDateTime.MIN
                    : c2.getDateDiscontinuation();
            retValue = dateDiscont1.compareTo(dateDiscont2);
        case COMPUTERINTRO:
            LocalDateTime dateIntro1 = c1.getDateIntroduction() == null ? LocalDateTime.MIN : c1.getDateIntroduction();
            LocalDateTime dateIntro2 = c2.getDateIntroduction() == null ? LocalDateTime.MIN : c2.getDateIntroduction();
            retValue = dateIntro1.compareTo(dateIntro2);
        case COMPUTERNAME:
            retValue = c1.getNom().compareTo(c2.getNom());
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
        compList.add(new Computer.ComputerBuilder(1, "PC 1").setDateIntroduction(LocalDateTime.now()).build());
        compList.add(new Computer.ComputerBuilder(2, "PC 2").setDateIntroduction(LocalDateTime.now()).build());
        MockitoAnnotations.initMocks(this);
        dao = mock(ComputerDAO.class);
        refreshMock();
    }

    @Test
    public void testGetComputerList() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        List<Computer> resList = comService.getComputerList(0, 20, OrderByColumn.COMPUTERID, true);
        List<Computer> resList2 = comService.getComputerList(1, 20, OrderByColumn.COMPUTERID, true);
        List<Computer> resList3 = comService.getComputerList(0, 1, OrderByColumn.COMPUTERID, true);
        assertEquals(compList, resList);
        assertEquals(compList.subList(1, compList.size()), resList2);
        assertEquals(compList.subList(0, 1), resList3);

    }

    @Test
    public void testGetComputerListBadStartIndex() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.getComputerList(-12, 20, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
        try {

            comService.getComputerList(compList.size() + 10, 20, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testGetComputerBadLimit() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.getComputerList(0, -123, OrderByColumn.COMPUTERID, true);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testGetComputerById() {
        ComputerService comService = new ComputerService(dao);
        Optional<Computer> c1 = comService.getComputerById(1);
        Optional<Computer> c2 = comService.getComputerById(2);
        assert (c1.isPresent());
        assert (c2.isPresent());
        assertEquals(compList.get(0), c1.get());
        assertEquals(compList.get(1), c2.get());

    }

    @Test
    public void testGetComputerByIdNoResult() {
        ComputerService comService = new ComputerService(dao);
        Optional<Computer> c1 = comService.getComputerById(78);
        Optional<Computer> c2 = comService.getComputerById(-100);
        assert (c1.isEmpty());
        assert (c2.isEmpty());
    }

    @Test
    public void testGetComputerByName() {
        ComputerService comService = new ComputerService(dao);
        Optional<Computer> c1 = comService.getComputerByName("PC 1");
        Optional<Computer> c2 = comService.getComputerByName("PC 2");
        assert (c1.isPresent());
        assert (c2.isPresent());
        assertEquals(compList.get(0), c1.get());
        assertEquals(compList.get(1), c2.get());

    }

    @Test
    public void testGetComputerByNameNoResult() {
        ComputerService comService = new ComputerService(dao);
        Optional<Computer> c1 = comService.getComputerByName("");
        Optional<Computer> c2 = comService.getComputerByName("Supercalculateur");
        assert (c1.isEmpty());
        assert (c2.isEmpty());
    }

    @Test
    public void testAddNewComputer() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        comService.buildNewComputer("PC de Test");
        comService.addComputerToDB();
        refreshMock();
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        assertEquals(toCompare, comService.getComputerByName("PC de Test").get());
    }

    @Test
    public void testAddNewComputerNoBuild() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.addComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testAddNewComputerFromAnother() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        comService.buildComputerForUpdate(toCompare);
        try {
            comService.addComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testUpdateComputer() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        Computer toCompare = new Computer.ComputerBuilder(dao.getMaxId(), "PC de Test").build();
        comService.buildComputerForUpdate(toCompare);
        comService.addIntroDate(LocalDateTime.of(2020, 1, 1, 0, 0));
        comService.updateComputerToDB();
        refreshMock();
        toCompare.setDateIntroduction(LocalDateTime.of(2020, 1, 1, 0, 0));
        assertEquals(toCompare, comService.getComputerByName("PC de Test").get());
    }

    @Test
    public void testUpdateComputerNoBuild() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.updateComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            //			e.printStackTrace();
        }
    }

    @Test
    public void testUpdateComputerFromNew() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        comService.buildNewComputer("PC de Test");
        try {
            comService.updateComputerToDB();
            assert (false);
        } catch (ComputerServiceException e) {
            //          e.printStackTrace();
        }
    }

    @Test
    public void testDeleteComputer() throws ComputerServiceException {
        ComputerService comService = new ComputerService(dao);
        comService.deleteComputer(1);
        assertEquals(compList, comService.getComputerList(0, 1000, OrderByColumn.COMPUTERID, true));
    }

    @Test
    public void testDeleteComputerNotExisting() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.deleteComputer(1234);
            assert (false);
        } catch (ComputerServiceException e) {
            //            e.printStackTrace();
        }
    }

    @Test
    public void testSearchComputerByName() {
        ComputerService comService = new ComputerService(dao);
        assertEquals(compList, comService.searchComputersByName("PC", OrderByColumn.COMPUTERID, true));
    }

    @Test
    public void testSearchComputerByNameNoResult() {
        ComputerService comService = new ComputerService(dao);
        assertEquals(Collections.EMPTY_LIST,
                comService.searchComputersByName("AZERTY", OrderByColumn.COMPUTERID, true));
    }

}
