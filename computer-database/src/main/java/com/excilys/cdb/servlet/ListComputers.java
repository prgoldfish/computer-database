package com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
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
        String addedComputer = req.getParameter("addedcomputer");
        if(addedComputer != null)
        {
            req.setAttribute("addedcomputer", addedComputer);
        }

        int pageLength = 10;
        String pageLengthParam = req.getParameter("length");
        int pageNum = 1;
        String pageNumParam = req.getParameter("page");
        if(pageLengthParam != null)
        {
            try 
            {
                pageLength = Math.max(1, Integer.parseInt(pageLengthParam));
            } catch (NumberFormatException nfe) {}
        }
        if(pageNumParam != null)
        {
            try 
            {
                pageNum = Math.max(1, Integer.parseInt(pageNumParam));
            } catch (NumberFormatException nfe) {}
        }
        
        int computerCount = 0;
        int startIndex = (pageNum - 1) * pageLength;        
        String searchParam = req.getParameter("search");
        
        List<Computer> listComputers = new ArrayList<Computer>();
        try {
            if(searchParam != null)
            {
                listComputers = service.searchComputersByName(searchParam);
                computerCount = listComputers.size();
                int lastPageIndex = computerCount - ((computerCount - (computerCount / pageLength) * pageLength)); 
                int startSubList = Math.min(startIndex, lastPageIndex);
                int endSubList = Math.min(startSubList + pageLength, computerCount);
                logger.info("lastPageIndex = " + lastPageIndex);
                logger.info("startSubList = " + startSubList);
                logger.info("endSubList = " + endSubList);
                listComputers = listComputers.subList(startSubList, endSubList);
            }
            else
            {
                listComputers = service.getComputerList(startIndex, pageLength);
                computerCount = service.getComputerList(0, Integer.MAX_VALUE).size();
            }
                      
        } catch (ComputerServiceException e) {
            throw new ServletException(e);
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
        

        long firstPageNum = pageNum < 4 ? 1 : pageNum - 2;
        long nbComputersAfter = computerCount - startIndex - pageLength;        
        long lastPageNum = nbComputersAfter > 2 * pageLength ? pageNum + 2 : ((computerCount - 1) / pageLength) + 1;

        req.setAttribute("dtolist", dtoList);
        req.setAttribute("dtosize", computerCount);
        req.setAttribute("search", searchParam);
        req.setAttribute("length", pageLength);
        req.setAttribute("page", pageNum);
        req.setAttribute("firstPageNum", firstPageNum);
        req.setAttribute("lastPageNum", lastPageNum);
        
        req.getRequestDispatcher("WEB-INF/views/dashboard.jsp").forward(req, resp);
    }

}
