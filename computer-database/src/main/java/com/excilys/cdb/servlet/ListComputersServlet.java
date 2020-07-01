package com.excilys.cdb.servlet;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.DashboardDTO;
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

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void defaultParams(ModelMap model) {
        if (model.getAttribute("params") == null) {
            model.addAttribute("params", new DashboardDTO());
        }
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET })
    public String dashboard(ModelMap model, @Valid DashboardDTO params, Locale loc) {
        /*@RequestParam(defaultValue = "1") int page,
         @RequestParam(defaultValue = "10") int length, @RequestParam(required = false) String search,
         @RequestParam(required = false) String headerMessage, @RequestParam(required = false) String order,
         @RequestParam(required = false) String ascendent,
         @RequestParam(required = false, name = "selection") String deleteString) {*/

        OrderByColumn orderColumn = OrderByColumn.getEnum(params.getOrder());
        boolean ascendentOrder = params.getAscendent() == null || !params.getAscendent().equals("desc");

        if (params.getSelection() != null) {
            int deletedCount = 0;
            for (String toDelete : params.getSelection().split(",")) {
                try {
                    int id = Integer.parseInt(toDelete);
                    computerService.deleteComputer(id);
                    deletedCount++;
                } catch (ComputerServiceException | NumberFormatException exc) {
                    logger.error(exc.getMessage());
                }
            }

            String message = messageSource.getMessage("header.message.deleted", null, "computer(s) deleted", loc);
            params.setHeaderMessage(deletedCount + " " + message);
        }
        int intPage = 1;
        int intLength = 10;
        try {
            intPage = Math.max(1, Integer.parseInt(params.getPage()));
            intLength = Math.max(1, Integer.parseInt(params.getLength()));
        } catch (NumberFormatException nfe) {
        }

        params.setLength(Integer.toString(intLength));
        params.setPage(Integer.toString(intPage));

        int computerCount = 0;
        Page<Computer> pages = null;

        try {
            if (params.getSearch() != null) {
                pages = new Page<>(
                        computerService.searchComputersByName(params.getSearch(), orderColumn, ascendentOrder),
                        intLength);
            } else {
                pages = new Page<>(computerService.getComputerList(0, Long.MAX_VALUE, orderColumn, ascendentOrder),
                        intLength);
            }
        } catch (ComputerServiceException cse) {
            logger.error(cse.getMessage());
        }

        pages.gotoPage(intPage);
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
        intPage = pages.getCurrentPage();

        model.addAttribute("params", params);

        model.addAttribute("dtolist", dtoList);
        model.addAttribute("dtosize", computerCount);
        //model.addAttribute("search", params.getSearch());
        //model.addAttribute("length", params.getLength());
        //model.addAttribute("page", params.getPage());
        model.addAttribute("firstPageNum", firstPageNum);
        model.addAttribute("lastPageNum", lastPageNum);
        //model.addAttribute("order", params.getOrder());
        //model.addAttribute("ascendent", params.getAscendent());

        return "dashboard";
    }

}
