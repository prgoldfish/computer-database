package com.excilys.cdb.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.excilys.cdb.CDBConfig;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDBConfig.class)
public class ListComputersServletTest {

    @Mock
    HttpServletRequest req;
    @Mock
    HttpServletResponse resp;

    @Autowired
    ComputerService computerService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
    public void ListComputersNoParameters() throws ServletException, IOException, ComputerServiceException {
        new ListComputersServlet().doGet(req, resp);
        List<ComputerDTO> expectedList = computerService.getComputerList(0, 10, OrderByColumn.COMPUTERID, true).stream()
                .map(c -> {
                    try {
                        return ComputerMapper.toDTO(c);
                    } catch (MapperException e) {
                        assert (false);
                        return null;
                    }
                }).collect(Collectors.toList());
        expectedAttributes.put("dtolist", expectedList);
        assertAttributeList();
    }

    private void assertAttributeList() {
        expectedAttributes.forEach((key, value) -> {
            assert (attributeList.containsKey(key));
            assertEquals(attributeList.get(key).getClass(), expectedTypesAttributes.get(key));
            assertEquals(attributeList.get(key), value);
        });
    }

    private static HashMap<String, Class<?>> initExpectedTypes() {
        HashMap<String, Class<?>> types = new HashMap<String, Class<?>>();
        types.put("dtolist", ArrayList.class);
        types.put("dtosize", Integer.class);
        types.put("search", String.class);
        types.put("length", Integer.class);
        types.put("page", Integer.class);
        types.put("firstPageNum", Integer.class);
        types.put("lastPageNum", Integer.class);
        types.put("order", String.class);
        types.put("ascendent", String.class);
        return types;

    }

}
