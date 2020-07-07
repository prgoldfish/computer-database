package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

import com.excilys.cdb.controller.ListComputersController;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.DashboardDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;

public class ListComputersServletTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    ComputerService mockService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ListComputersController servlet;

    Map<String, Object> expectedAttributes;
    ModelMap attributeList;
    static Map<String, Class<?>> expectedTypesAttributes = initExpectedTypes();

    DashboardDTO params;

    Locale loc;

    @Before
    public void setUp() throws Exception {
        expectedAttributes = new HashMap<String, Object>();
        attributeList = new ModelMap();
        params = new DashboardDTO();
        loc = Locale.ROOT;
    }

    @Test
    public void ListComputersDefaultParametersTest() throws ComputerServiceException {
        DashboardDTO expected = new DashboardDTO();
        assertEquals(servlet.dashboard(attributeList, params, loc), "dashboard");
        verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("params", expected);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        assertAttributeList();
    }

    @Test
    public void ListComputersAllOrderByTest() throws ServletException, IOException, ComputerServiceException {
        List<String> orderBys = Arrays.asList("ComputerName", "IntroducedDate", "DiscontinuedDate", "CompanyName");
        List<String> ascendentOrders = Arrays.asList("asc", "desc");

        for (String orderBy : orderBys) {
            for (String ascendent : ascendentOrders) {
                DashboardDTO expected = new DashboardDTO();
                params.setOrder(orderBy);
                params.setAscendent(ascendent);
                attributeList.clear();
                servlet.dashboard(attributeList, params, loc);
                verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.getEnum(orderBy),
                        !ascendent.equals("desc"));
                expectedAttributes.clear();
                expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
                expectedAttributes.put("dtosize", 0);
                expectedAttributes.put("firstPageNum", 1L);
                expectedAttributes.put("lastPageNum", 1L);
                expected.setOrder(orderBy);
                expected.setAscendent(ascendent);
                expectedAttributes.put("params", expected);
                assertAttributeList();
            }
        }
    }

    @Test
    public void ListComputersUnknownOrderByParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setOrder("unknown");
        DashboardDTO expected = new DashboardDTO();
        expected.setOrder("unknown");
        servlet.dashboard(attributeList, params, loc);
        verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("params", expected);
        assertAttributeList();
    }

    @Test
    public void ListComputersUnknownAscendentParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setAscendent("unknown");
        DashboardDTO expected = new DashboardDTO();
        expected.setAscendent("unknown");
        servlet.dashboard(attributeList, params, loc);
        verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("params", expected);
        assertAttributeList();
    }

    @Test
    public void ListComputersPageLengthParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setLength("50");
        DashboardDTO expected = new DashboardDTO();
        expected.setLength("50");

        List<Computer> answerList = new ArrayList<>();
        List<ComputerDTO> expectedList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            answerList.add(new Computer.ComputerBuilder(i, "NoName " + i).build());
            expectedList.add(new ComputerDTO.ComputerBuilderDTO("" + i, "NoName " + i).build());
        }
        when(mockService.getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true)).thenReturn(answerList);

        servlet.dashboard(attributeList, params, loc);
        expectedAttributes.put("dtolist", expectedList.subList(0, 50));
        expectedAttributes.put("dtosize", 1000);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 3L);
        expectedAttributes.put("params", expected);
        assertAttributeList();
    }

    @Test
    public void ListComputersPageParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setPage("3");
        DashboardDTO expected = new DashboardDTO();
        expected.setPage("3");

        List<Computer> answerList = new ArrayList<>();
        List<ComputerDTO> expectedList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            answerList.add(new Computer.ComputerBuilder(i, "NoName " + i).build());
            expectedList.add(new ComputerDTO.ComputerBuilderDTO("" + i, "NoName " + i).build());
        }
        when(mockService.getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true)).thenReturn(answerList);

        servlet.dashboard(attributeList, params, loc);
        expectedAttributes.put("dtolist", expectedList.subList(20, 30));
        expectedAttributes.put("dtosize", 1000);
        expectedAttributes.put("params", expected);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 5L);
        assertAttributeList();
    }

    @Test
    public void ListComputersSearchParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setSearch("Nin");
        DashboardDTO expected = new DashboardDTO();
        expected.setSearch("Nin");

        servlet.dashboard(attributeList, params, loc);
        verify(mockService).searchComputersByName("Nin", OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("params", expected);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        assertAttributeList();
    }

    @Test
    public void ListComputersHeaderMessageParameterTest() throws ServletException, IOException, ComputerServiceException {
        params.setHeaderMessage("Test");
        DashboardDTO expected = new DashboardDTO();
        expected.setHeaderMessage("Test");

        servlet.dashboard(attributeList, params, loc);
        verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("params", expected);
        assertAttributeList();
    }

    @Test
    public void ListComputersDeleteTest() throws ComputerServiceException, ServletException, IOException {
        when(messageSource.getMessage(anyString(), isNull(), anyString(), any(Locale.class)))
                .thenReturn("ordinateur(s) supprimés");
        params.setSelection("1,2,3");
        DashboardDTO expected = new DashboardDTO();
        expected.setSelection("1,2,3");
        expected.setHeaderMessage("3 ordinateur(s) supprimés");

        servlet.dashboard(attributeList, params, loc);
        verify(mockService).getComputerList(0, Integer.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        verify(mockService).deleteComputer(1);
        verify(mockService).deleteComputer(2);
        verify(mockService).deleteComputer(3);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("params", expected);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);

    }

    private void assertAttributeList() {
        expectedAttributes.forEach((key, value) -> {
            assert (attributeList.containsKey(key));
            assertEquals("Attribute name : " + key, expectedTypesAttributes.get(key),
                    attributeList.get(key).getClass());
            assertEquals("Attribute name : " + key, value, attributeList.get(key));
        });
    }

    private static HashMap<String, Class<?>> initExpectedTypes() {
        HashMap<String, Class<?>> types = new HashMap<String, Class<?>>();
        types.put("dtolist", ArrayList.class);
        types.put("dtosize", Integer.class);
        types.put("firstPageNum", Long.class);
        types.put("lastPageNum", Long.class);
        types.put("params", DashboardDTO.class);
        return types;

    }
}
