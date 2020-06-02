package com.excilys.cdb.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.ComputerDAO;

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
        when(dao.getComputerList(anyLong(), anyLong())).thenAnswer(invoc -> {
            int startIndex = invoc.getArgument(0, Long.class).intValue();
            int limit = invoc.getArgument(1, Long.class).intValue();
            if (startIndex >= 0 && startIndex < compList.size() && limit > 0) {
                if (startIndex + limit < compList.size()) {
                    return compList.subList(startIndex, startIndex + limit);
                } else {
                    return compList.subList(startIndex, compList.size());
                }
            }
            return new ArrayList<>();
        });
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
        List<Computer> resList = comService.getComputerList(0, 20);
        List<Computer> resList2 = comService.getComputerList(1, 20);
        List<Computer> resList3 = comService.getComputerList(0, 1);
        assertEquals(compList, resList);
        assertEquals(compList.subList(1, compList.size()), resList2);
        assertEquals(compList.subList(0, 1), resList3);

    }

    @Test
    public void testGetComputerListBadStartIndex() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.getComputerList(-12, 20);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
        try {
            
            comService.getComputerList(compList.size() + 10, 20);
            assert (false);
        } catch (ComputerServiceException e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testGetComputerBadLimit() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.getComputerList(0, -123);
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
        comService.buildComputerFromScratch("PC de Test");
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
        comService.buildComputerFromComputer(toCompare);
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
        comService.buildComputerFromComputer(toCompare);
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
        comService.buildComputerFromScratch("PC de Test");
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
        assertEquals(compList, comService.getComputerList(0, 1000));
    }
    
    
    @Test
    public void testDeleteComputerNotExisting() {
        ComputerService comService = new ComputerService(dao);
        try {
            comService.deleteComputer(1234);
            assert(false);
        } catch (ComputerServiceException e) {
//            e.printStackTrace();
        }
    }

}
