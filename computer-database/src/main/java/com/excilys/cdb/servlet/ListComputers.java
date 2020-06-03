package com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.service.ComputerService;

@WebServlet("/ListComputers")
public class ListComputers extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -3042238239381847969L;
    private static final Logger logger = LoggerFactory.getLogger(ListComputers.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        HttpSession session = req.getSession();
        ComputerDAO dao = (ComputerDAO) session.getAttribute("computerdao");
        if(dao == null)
        {
            dao = new ComputerDAO();
            session.setAttribute("computerdao", dao);
        }
        ComputerService service = (ComputerService) session.getAttribute("computerservice");
        if(service == null)
        {
            service = new ComputerService(dao);
            session.setAttribute("computerservice", service);
        }
        
        String searchParam = req.getParameter("search");
        List<Computer> listComputers = new ArrayList<Computer>();
        try {
            if(searchParam != null)
            {
                listComputers = service.searchComputersByName(searchParam);
            }
            else
            {
                listComputers = service.getComputerList(0, Integer.MAX_VALUE);  
            }
                      
        } catch (ComputerServiceException e) {
            throw new ServletException(e);
            //e.printStackTrace();
        }
        
        List<ComputerDTO> dtoList = listComputers.stream().map(c -> {
            try {
                return ComputerMapper.toDTO(c);
            } catch (MapperException e) {
                logger.error("Null Computer found in computer list (shoud not happen)");
            }
            return null;
        }).filter(dto -> dto != null)
        .collect(Collectors.toList());

        req.setAttribute("dtolist", dtoList);
        req.setAttribute("dtosize", dtoList.size());
        
        req.getRequestDispatcher("WEB-INF/views/dashboard.jsp").forward(req, resp);
    }

}
