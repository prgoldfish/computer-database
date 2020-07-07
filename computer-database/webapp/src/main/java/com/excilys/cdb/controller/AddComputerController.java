package com.excilys.cdb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;
import com.excilys.cdb.dto.DashboardDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.CompanyMapper;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.validation.ComputerDTOValidator;

@Controller
@RequestMapping("/AddComputer")
public class AddComputerController {

    //private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);

    @Autowired
    ComputerService computerService;
    @Autowired
    CompanyService companyService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ComputerDTOValidator computerValidator;

    @ModelAttribute
    public void getCompanyDTO(ModelMap model, @RequestParam(required = false) Long companyId) {
        if (companyId == null) {
            return;
        }
        Optional<Company> optComp = companyService.getCompanyById(companyId);
        model.addAttribute("companyDTO", optComp.map(c -> {
            try {
                return CompanyMapper.toDTO(c);
            } catch (MapperException e) { //Cannot happen
                return null;
            }
        }).orElse(null));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getCompaniesList(ModelMap model) {
        model.addAttribute("computerDto", new ComputerBuilderDTO("", ""));
        String headerMessage = (String) model.getAttribute("headerMessage");
        if (headerMessage == null || headerMessage.isEmpty()) {
            List<Company> companyList = companyService.getCompaniesList();
            model.addAttribute("companies", companyList);
        }
        return "addComputer";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addComputer(ModelMap model, @Valid ComputerBuilderDTO builder, @RequestParam String companyId,
            BindingResult br, Locale loc) {

        String jspRet = getCompaniesList(model);
        List<String> errorMessages = new ArrayList<>();

        CompanyDTO comp = (CompanyDTO) model.getAttribute("companyDTO");
        ComputerBuilderDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(
                Long.toString(computerService.getMaxId() + 1), builder.getNom())
                        .setDateIntroduction(builder.getDateIntroduction())
                        .setDateDiscontinuation(builder.getDateDiscontinuation()).setEntreprise(comp);

        Computer com = null;

        model.addAttribute("computerDto", dtoComputer);
        computerValidator.validate(dtoComputer, br);
        if (br.hasErrors()) {
            errorMessages = computerValidator.getErrorList();
            model.addAttribute("errors", errorMessages);
            return jspRet;
        }

        try {
            com = ComputerMapper.fromDTO(dtoComputer.build());
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }

        if (errorMessages.isEmpty()) {
            try {
                computerService.addNewComputer(com);
                String message = messageSource.getMessage("header.message.added", null,
                        "The computer has successfully been added to the database", loc);
                model.addAttribute("headerMessage", message);
                model.addAttribute("dashboardDTO", new DashboardDTO());
            } catch (ComputerServiceException cse) {
                errorMessages.add(cse.getMessage());
            }
        }
        model.addAttribute("errors", errorMessages);
        return jspRet;
    }
}
