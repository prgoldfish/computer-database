package com.excilys.cdb.servlet;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.Page;

@Controller
@RequestMapping("/ListComputers")
public class ListComputersServlet {

    private static final Logger logger = LoggerFactory.getLogger(ListComputersServlet.class);

    @Autowired
    private ComputerService computerService;

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET })
    public String dashboard(ModelMap model, @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") String length, @RequestParam(required = false) String search,
            @RequestParam(required = false) String headerMessage, @RequestParam(required = false) String order,
            @RequestParam(required = false) String ascendent,
            @RequestParam(required = false, name = "selection") String deleteString) {

        OrderByColumn orderColumn = OrderByColumn.getEnum(order);
        boolean ascendentOrder = ascendent == null || !ascendent.equals("desc");

        if (headerMessage != null) {
            model.addAttribute("headerMessage", headerMessage);
        }

        if (deleteString != null) {
            int deletedCount = 0;
            for (String toDelete : deleteString.split(",")) {
                try {
                    int id = Integer.parseInt(toDelete);
                    computerService.deleteComputer(id);
                    deletedCount++;
                } catch (ComputerServiceException | NumberFormatException exc) {
                    logger.error(exc.getMessage());
                }
            }
            model.addAttribute("headerMessage", deletedCount + " computer(s) deleted");
        }

        int pageLength = 10;
        int pageNum = 1;
        if (length != null) {
            try {
                pageLength = Math.max(1, Integer.parseInt(length));
            } catch (NumberFormatException nfe) {
            }
        }
        if (page != null) {
            try {
                pageNum = Math.max(1, Integer.parseInt(page));
            } catch (NumberFormatException nfe) {
            }
        }

        int computerCount = 0;
        Page<Computer> pages = null;

        try {
            if (search != null) {
                pages = new Page<>(computerService.searchComputersByName(search, orderColumn, ascendentOrder),
                        pageLength);
            } else {
                pages = new Page<>(computerService.getComputerList(0, Long.MAX_VALUE, orderColumn, ascendentOrder),
                        pageLength);
            }
        } catch (ComputerServiceException cse) {
            logger.error(cse.getMessage());
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

        model.addAttribute("dtolist", dtoList);
        model.addAttribute("dtosize", computerCount);
        model.addAttribute("search", search);
        model.addAttribute("length", pageLength);
        model.addAttribute("page", pageNum);
        model.addAttribute("firstPageNum", firstPageNum);
        model.addAttribute("lastPageNum", lastPageNum);
        model.addAttribute("order", order);
        model.addAttribute("ascendent", ascendent);

        return "dashboard";
    }

}
