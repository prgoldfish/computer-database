package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ModelMap;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.springconfig.CDBConfig;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDBConfig.class)
public class ListComputersServletTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    ComputerService mockService;

    @InjectMocks
    ListComputersServlet servlet;

    @Autowired
    ComputerService computerService;

    Map<String, Object> expectedAttributes;
    ModelMap attributeList;
    static Map<String, Class<?>> expectedTypesAttributes = initExpectedTypes();

    @Before
    public void setUp() throws Exception {
        expectedAttributes = new HashMap<String, Object>();
        attributeList = new ModelMap();
    }

    @Test
    public void ListComputersDefaultParametersTest() throws ComputerServiceException {
        assertEquals(servlet.dashboard(attributeList, 1, 10, null, null, null, null, null), "dashboard");
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
    public void ListComputersAllOrderByTest() throws ServletException, IOException, ComputerServiceException {
        List<String> orderBys = Arrays.asList("ComputerName", "IntroducedDate", "DiscontinuedDate", "CompanyName");
        List<String> ascendentOrders = Arrays.asList("asc", "desc");

        for (String orderBy : orderBys) {
            for (String ascendent : ascendentOrders) {
                attributeList.clear();
                servlet.dashboard(attributeList, 1, 10, null, null, orderBy, ascendent, null);
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
        servlet.dashboard(attributeList, 1, 10, null, null, "unknown", null, null);
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
        servlet.dashboard(attributeList, 1, 10, null, null, null, "unknown", null);
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

        servlet.dashboard(attributeList, 1, 50, null, null, null, null, null);
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

        servlet.dashboard(attributeList, 3, 10, null, null, null, null, null);
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
        servlet.dashboard(attributeList, 1, 10, "Nin", null, null, null, null);
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
        servlet.dashboard(attributeList, 1, 10, null, "test", null, null, null);
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
        servlet.dashboard(attributeList, 1, 10, null, null, null, null, "1,2,3");
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
