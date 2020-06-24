package com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.CompanyMapper;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;

@Controller
@RequestMapping("/AddComputer")
public class AddComputerServlet {

    //private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);

    @Autowired
    ComputerService computerService;
    @Autowired
    CompanyService companyService;

    @RequestMapping(method = RequestMethod.GET)
    public String getCompaniesList(ModelMap model) throws ServletException, IOException {
        String headerMessage = (String) model.getAttribute("headerMessage");
        if (headerMessage == null || headerMessage.isEmpty()) {
            List<Company> companyList = companyService.getCompaniesList();
            model.addAttribute("companies", companyList);
        }
        return "addComputer";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComputer(ModelMap model, @RequestParam String computerName, @RequestParam String introduced,
            @RequestParam String discontinued, @RequestParam String companyId) throws ServletException, IOException {

        List<String> errorMessages = new ArrayList<>();

        CompanyDTO comp = null;
        try {
            int id = Integer.parseInt(companyId);
            Optional<Company> optComp = companyService.getCompanyById(id);
            if (optComp.isPresent()) {
                comp = new CompanyDTO(companyId, CompanyMapper.toDTO(optComp.get()).getNom());
            }
        } catch (NumberFormatException | MapperException nfe) {
        }

        ComputerDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(Long.toString(computerService.getMaxId() + 1),
                computerName).setDateIntroduction(introduced).setDateDiscontinuation(discontinued).setEntreprise(comp)
                        .build();

        Computer com = null;
        try {
            com = ComputerMapper.fromDTO(dtoComputer);
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }

        if (errorMessages.isEmpty()) {
            try {
                computerService.addNewComputer(com);
                model.addAttribute("headerMessage", "The computer has successfully been added to the database");
            } catch (ComputerServiceException cse) {
                errorMessages.add(cse.getMessage());
            }
        }
        model.addAttribute("errors", errorMessages);
        return getCompaniesList(model);

    }
}
