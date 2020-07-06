package com.excilys.cdb.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.dto.ComputerDTO.ComputerBuilderDTO;
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
@RequestMapping("/EditComputer")
public class EditComputerServlet {

    private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);

    @Autowired
    private ComputerService computerService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    ComputerDTOValidator computerValidator;

    @Autowired
    MessageSource messageSource;

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

    private String redirectToDashboard() {
        return "redirect:ListComputers";
    }

    @GetMapping
    public String getComputerInfo(ModelMap model, @RequestParam long id) {
        if (id <= 0) {
            return redirectToDashboard();
        }
        Optional<Computer> optComp = computerService.getComputerById(id);
        if (optComp.isEmpty()) {
            return redirectToDashboard();
        }
        ComputerDTO c;
        try {
            c = ComputerMapper.toDTO(optComp.get());
        } catch (MapperException e) {
            logger.error(e.getMessage());
            return redirectToDashboard();
        }
        setComputerAttributes(model, c, id);
        model.addAttribute("companies", companyService.getCompaniesList());
        model.addAttribute("computerDto", new ComputerBuilderDTO("", ""));
        return "editComputer";
    }

    @PostMapping
    public String editComputer(ModelMap model, @Valid ComputerBuilderDTO builder,
            @RequestParam(name = "companyId") long companyId, BindingResult br, Locale loc) {

        List<String> errorMessages = new ArrayList<>();
        if (Long.valueOf(builder.getId()) <= 0) {
            return redirectToDashboard();
        }
        CompanyDTO comp = (CompanyDTO) model.getAttribute("companyDTO");
        ComputerBuilderDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(builder.getId(), builder.getNom())
                .setDateIntroduction(builder.getDateIntroduction())
                .setDateDiscontinuation(builder.getDateDiscontinuation()).setEntreprise(comp);

        setComputerAttributes(model, dtoComputer.build(), Long.valueOf(builder.getId()));

        model.addAttribute("computerDto", dtoComputer);
        computerValidator.validate(dtoComputer, br);
        if (br.hasErrors()) {
            errorMessages = computerValidator.getErrorList();
            logger.debug("Error in the edit validation");
            model.addAttribute("errors", errorMessages);
            return "editComputer";
        }

        Computer com = null;
        try {
            com = ComputerMapper.fromDTO(dtoComputer.build());
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }
        if (errorMessages.isEmpty()) {
            try {
                computerService.updateComputer(com);
                String message = messageSource.getMessage("header.message.edited", null,
                        "The computer has successfully been edited", loc);
                model.addAttribute("headerMessage", message);
            } catch (ComputerServiceException cse) {
                errorMessages.add(cse.getMessage());
            }
        }
        if (model.getAttribute("headerMessage") == null) {
            model.addAttribute("companies", companyService.getCompaniesList());
        }

        return "editComputer";
    }

    private void setComputerAttributes(ModelMap model, ComputerDTO c, long id) {
        model.addAttribute("computerName", c.getNom());
        model.addAttribute("dateIntro", c.getDateIntroduction());
        model.addAttribute("dateDiscont", c.getDateDiscontinuation());
        model.addAttribute("companyId", c.getEntreprise() != null ? c.getEntreprise().getId() : 0);
        model.addAttribute("id", id);
    }
}
