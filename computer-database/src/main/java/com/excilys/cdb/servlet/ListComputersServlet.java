package com.excilys.cdb.servlet;

import java.io.IOException;
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
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.Page;

@WebServlet("/ListComputers")
public class ListComputersServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -3042238239381847969L;
    private static final Logger logger = LoggerFactory.getLogger(ListComputersServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String orderParam = req.getParameter("order");
        OrderByColumn orderColumn = getOrder(orderParam);
        String ascendentOrderParam = req.getParameter("ascendent");
        boolean ascendentOrder = ascendentOrderParam == null || !ascendentOrderParam.equals("desc");

        ComputerDAO dao = (ComputerDAO) session.getAttribute("computerdao");
        if (dao == null) {
            dao = new ComputerDAO();
            session.setAttribute("computerdao", dao);
        }
        ComputerService service = (ComputerService) session.getAttribute("computerservice");
        if (service == null) {
            service = new ComputerService(dao);
            session.setAttribute("computerservice", service);
        }
        String headerMessage = req.getParameter("headerMessage");
        if (headerMessage != null) {
            req.setAttribute("headerMessage", headerMessage);
        }

        String selection = req.getParameter("selection");
        if (selection != null) {
            int deletedCount = 0;
            for (String toDelete : selection.split(",")) {
                try {
                    int id = Integer.parseInt(toDelete);
                    service.deleteComputer(id);
                    deletedCount++;
                } catch (ComputerServiceException | NumberFormatException exc) {
                    logger.error(exc.getMessage());
                }
            }
            req.setAttribute("headerMessage", deletedCount + " computer(s) deleted");

        }

        int pageLength = 10;
        String pageLengthParam = req.getParameter("length");
        int pageNum = 1;
        String pageNumParam = req.getParameter("page");
        if (pageLengthParam != null) {
            try {
                pageLength = Math.max(1, Integer.parseInt(pageLengthParam));
            } catch (NumberFormatException nfe) {
            }
        }
        if (pageNumParam != null) {
            try {
                pageNum = Math.max(1, Integer.parseInt(pageNumParam));
            } catch (NumberFormatException nfe) {
            }
        }

        int computerCount = 0;
        String searchParam = req.getParameter("search");
        Page<Computer> pages = null;

        try {
            if (searchParam != null) {
                pages = new Page<>(service.searchComputersByName(searchParam, orderColumn, ascendentOrder), pageLength);
            } else {
                pages = new Page<>(service.getComputerList(0, Long.MAX_VALUE, orderColumn, ascendentOrder), pageLength);
            }
        } catch (ComputerServiceException e) {
            throw new ServletException(e);
        }

        pages.gotoPage(pageNum);
        computerCount = pages.getElementCount();

        List<ComputerDTO> dtoList = pages.getPageContent().stream().map(c -> {
            try {
                return ComputerMapper.toDTO(c);
            } catch (MapperException e) {
                logger.error("Null Computer found in computer list (shoud not happen)");
            }
            return null;
        }).filter(dto -> dto != null).collect(Collectors.toList());

        long firstPageNum = Math.max(pages.getCurrentPage() - 2, 1);
        long lastPageNum = Math.min(pages.getCurrentPage() + 2, pages.getMaxPage());
        pageNum = pages.getCurrentPage();

        req.setAttribute("dtolist", dtoList);
        req.setAttribute("dtosize", computerCount);
        req.setAttribute("search", searchParam);
        req.setAttribute("length", pageLength);
        req.setAttribute("page", pageNum);
        req.setAttribute("firstPageNum", firstPageNum);
        req.setAttribute("lastPageNum", lastPageNum);
        req.setAttribute("order", orderParam);
        req.setAttribute("ascendent", ascendentOrderParam);

        req.getRequestDispatcher("WEB-INF/views/dashboard.jsp").forward(req, resp);
    }

    private OrderByColumn getOrder(String orderParam) {
        if (orderParam == null) {
            return OrderByColumn.COMPUTERID;
        }
        switch (orderParam) {
        case "ComputerName":
            return OrderByColumn.COMPUTERNAME;
        case "IntroducedDate":
            return OrderByColumn.COMPUTERINTRO;
        case "DiscontinuedDate":
            return OrderByColumn.COMPUTERDISCONT;
        case "CompanyName":
            return OrderByColumn.COMPANYNAME;
        default:
            return OrderByColumn.COMPUTERID;
        }
    }

}
