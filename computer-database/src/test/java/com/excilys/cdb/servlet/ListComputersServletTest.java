package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.excilys.cdb.CDBConfig;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;

@RunWith(MockitoJUnitRunner.class)
public class ListComputersServletTest {

    @Mock
    HttpServletRequest req;
    @Mock
    HttpServletResponse resp;
    @Mock
    ComputerService mockService;

    @InjectMocks
    ListComputersServlet servlet;

    ComputerService computerService = CDBConfig.getContext().getBean(ComputerService.class);

    Map<String, Object> expectedAttributes;
    Map<String, Object> attributeList;
    static Map<String, Class<?>> expectedTypesAttributes = initExpectedTypes();

    @Before
    public void setUp() throws Exception {
        expectedAttributes = new HashMap<String, Object>();
        attributeList = new HashMap<String, Object>();
        doAnswer(invoc -> attributeList.put(invoc.getArgument(0), invoc.getArgument(1))).when(req)
                .setAttribute(anyString(), any());
        RequestDispatcher rd = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher(anyString())).thenReturn(rd);
    }

    @Test
    public void ListComputersNoParametersTest() throws ServletException, IOException, ComputerServiceException {
        servlet.doGet(req, resp);
        verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        assertAttributeList();
    }

    @Test
    public void ListComputersOrderByComputerNameAscTest() throws ServletException, IOException, ComputerServiceException {
        List<String> orderBys = Arrays.asList("ComputerName", "IntroducedDate", "DiscontinuedDate", "CompanyName");
        List<String> ascendentOrders = Arrays.asList("asc", "desc");

        for (String orderBy : orderBys) {
            for (String ascendent : ascendentOrders) {
                when(req.getParameter("order")).thenReturn(orderBy);
                if (ascendent.equals("desc")) {
                    when(req.getParameter("ascendent")).thenReturn(ascendent);
                } else {
                    when(req.getParameter("ascendent")).thenReturn(null);
                }
                servlet.doGet(req, resp);
                verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.getEnum(orderBy),
                        !ascendent.equals("desc"));
                expectedAttributes.clear();
                expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
                expectedAttributes.put("dtosize", 0);
                expectedAttributes.put("length", 10);
                expectedAttributes.put("page", 1);
                expectedAttributes.put("firstPageNum", 1L);
                expectedAttributes.put("lastPageNum", 1L);
                expectedAttributes.put("order", orderBy);
                if (ascendent.equals("desc")) {
                    expectedAttributes.put("ascendent", ascendent);
                }
                assertAttributeList();
            }
        }
    }

    @Test
    public void ListComputersUnknownOrderByParameterTest() throws ServletException, IOException, ComputerServiceException {
        when(req.getParameter("order")).thenReturn("unknown");
        servlet.doGet(req, resp);
        verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("order", "unknown");
        assertAttributeList();
    }

    @Test
    public void ListComputersUnknownAscendentParameterTest() throws ServletException, IOException, ComputerServiceException {
        when(req.getParameter("ascendent")).thenReturn("unknown");
        servlet.doGet(req, resp);
        verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("ascendent", "unknown");
        assertAttributeList();
    }

    @Test
    public void ListComputersPageLengthParameterTest() throws ServletException, IOException, ComputerServiceException {
        List<Computer> answerList = new ArrayList<>();
        List<ComputerDTO> expectedList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            answerList.add(new Computer.ComputerBuilder(i, "NoName " + i).build());
            expectedList.add(new ComputerDTO.ComputerBuilderDTO("" + i, "NoName " + i).build());
        }
        when(mockService.getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true)).thenReturn(answerList);

        when(req.getParameter("length")).thenReturn("50");
        servlet.doGet(req, resp);
        expectedAttributes.put("dtolist", expectedList.subList(0, 50));
        expectedAttributes.put("dtosize", 1000);
        expectedAttributes.put("length", 50);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 3L);
        assertAttributeList();
    }

    @Test
    public void ListComputersPageParameterTest() throws ServletException, IOException, ComputerServiceException {
        List<Computer> answerList = new ArrayList<>();
        List<ComputerDTO> expectedList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            answerList.add(new Computer.ComputerBuilder(i, "NoName " + i).build());
            expectedList.add(new ComputerDTO.ComputerBuilderDTO("" + i, "NoName " + i).build());
        }
        when(mockService.getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true)).thenReturn(answerList);

        when(req.getParameter("page")).thenReturn("3");
        servlet.doGet(req, resp);
        expectedAttributes.put("dtolist", expectedList.subList(20, 30));
        expectedAttributes.put("dtosize", 1000);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 3);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 5L);
        assertAttributeList();
    }

    @Test
    public void ListComputersSearchParameterTest() throws ServletException, IOException, ComputerServiceException {
        when(req.getParameter("search")).thenReturn("Nin");
        servlet.doGet(req, resp);
        verify(mockService).searchComputersByName("Nin", OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("search", "Nin");
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        assertAttributeList();
    }

    @Test
    public void ListComputersHeaderMessageParameterTest() throws ServletException, IOException, ComputerServiceException {
        when(req.getParameter("headerMessage")).thenReturn("test");
        servlet.doGet(req, resp);
        verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
        expectedAttributes.put("firstPageNum", 1L);
        expectedAttributes.put("lastPageNum", 1L);
        expectedAttributes.put("headerMessage", "test");
        assertAttributeList();
    }

    @Test
    public void ListComputersDeleteTest() throws ComputerServiceException, ServletException, IOException {
        when(req.getParameter("selection")).thenReturn("1,2,3");
        servlet.doGet(req, resp);
        verify(mockService).getComputerList(0, Long.MAX_VALUE, OrderByColumn.COMPUTERID, true);
        verify(mockService).deleteComputer(1);
        verify(mockService).deleteComputer(2);
        verify(mockService).deleteComputer(3);
        expectedAttributes.put("dtolist", new ArrayList<ComputerDTO>());
        expectedAttributes.put("dtosize", 0);
        expectedAttributes.put("length", 10);
        expectedAttributes.put("page", 1);
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
        types.put("search", String.class);
        types.put("length", Integer.class);
        types.put("page", Integer.class);
        types.put("firstPageNum", Long.class);
        types.put("lastPageNum", Long.class);
        types.put("order", String.class);
        types.put("ascendent", String.class);
        types.put("headerMessage", String.class);
        return types;

    }

}
