package com.excilys.cdb.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.DashboardDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.PageManager;
import com.excilys.cdb.validation.ComputerDTOValidator;

@RestController
public class ComputerRestController {

    @Autowired
    ComputerService computerService;
    @Autowired
    CompanyService companyService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ComputerDTOValidator computerValidator;

    private static final Logger logger = LoggerFactory.getLogger(ComputerRestController.class);

    @GetMapping("rest/computer/get/{id}")
    public ResponseEntity<Object> getComputerById(@PathVariable long id) throws MapperException {
        Optional<Computer> optComputer = computerService.getComputerById(id);
        if (optComputer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No computer with id " + id + " found");
        }
        //return optComputer.get();
        return ResponseEntity.ok().body(ComputerMapper.toDTO(optComputer.get()));
    }

    @GetMapping("rest/computer/list")
    public ResponseEntity<Object> getComputerList(DashboardDTO options) throws ComputerServiceException {
        boolean ascendent = !"desc".equalsIgnoreCase(options.getAscendent());
        try {
            PageManager page = new PageManager(Integer.valueOf(options.getPage()),
                    Integer.valueOf(options.getLength()));
            List<Computer> compList = null;
            if (options.getSearch() != null) {
                compList = page.getSubList(computerService.searchComputersByName(options.getSearch(),
                        OrderByColumn.getEnum(options.getOrder()), ascendent));
            } else {
                compList = computerService.getComputerList(page.getOffset(), page.getLength(),
                        OrderByColumn.getEnum(options.getOrder()), ascendent);
            }

            List<ComputerDTO> dtoList = compList.stream().map(c -> {
                try {
                    return ComputerMapper.toDTO(c);
                } catch (MapperException e) {
                    logger.error("Null Computer found in computer list (shoud not happen)");
                }
                return null;
            }).filter(dto -> dto != null).collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);
        } catch (IllegalArgumentException iae) {
            logger.debug("IllegalArgumentException", iae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (ComputerServiceException cse) {
            return ResponseEntity.ok(Collections.EMPTY_LIST);
        }
    }
    /*
    @PostMapping("rest/computer/add")
    public HttpHeaders addComputer(@Valid ComputerBuilderDTO builder) {
    
    }
    */
}
