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
    public String dashboard(ModelMap model, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int length, @RequestParam(required = false) String search,
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

        length = Math.max(1, length);
        page = Math.max(1, page);

        int computerCount = 0;
        Page<Computer> pages = null;

        try {
            if (search != null) {
                pages = new Page<>(computerService.searchComputersByName(search, orderColumn, ascendentOrder), length);
            } else {
                pages = new Page<>(computerService.getComputerList(0, Long.MAX_VALUE, orderColumn, ascendentOrder),
                        length);
            }
        } catch (ComputerServiceException cse) {
            logger.error(cse.getMessage());
        }

        pages.gotoPage(page);
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
        page = pages.getCurrentPage();

        model.addAttribute("dtolist", dtoList);
        model.addAttribute("dtosize", computerCount);
        model.addAttribute("search", search);
        model.addAttribute("length", length);
        model.addAttribute("page", page);
        model.addAttribute("firstPageNum", firstPageNum);
        model.addAttribute("lastPageNum", lastPageNum);
        model.addAttribute("order", order);
        model.addAttribute("ascendent", ascendent);

        return "dashboard";
    }

}
