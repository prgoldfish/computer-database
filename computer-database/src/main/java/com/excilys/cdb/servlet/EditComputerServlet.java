package com.excilys.cdb.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;

@Controller
@RequestMapping("/EditComputer")
public class EditComputerServlet {

    private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);

    @Autowired
    private ComputerService computerService;
    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String redirectToDashboard() {
        return "redirect:ListComputers";
    }

    @PostMapping(params = "id")
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

        return "editComputer";
    }

    @PostMapping(params = { "id", "computerName", "introduced", "discontinued", "companyId" })
    public String editComputer(ModelMap model, @RequestParam long id, @RequestParam String computerName,
            @RequestParam String introduced, @RequestParam String discontinued, @RequestParam long companyId) {

        List<String> errorMessages = new ArrayList<>();
        if (id <= 0) {
            return redirectToDashboard();
        }
        CompanyDTO comp = getCompanyDTO(companyId);
        ComputerDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(Long.toString(id), computerName)
                .setDateIntroduction(introduced).setDateDiscontinuation(discontinued).setEntreprise(comp).build();

        setComputerAttributes(model, dtoComputer, id);

        Computer com = null;
        try {
            com = ComputerMapper.fromDTO(dtoComputer);
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }
        if (errorMessages.isEmpty()) {
            try {
                computerService.updateComputer(com);
                model.addAttribute("headerMessage", "The computer has successfully been edited");
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

    /*public String doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> errorMessages = new ArrayList<>();
        String computerName = req.getParameter("computerName");
        String introducedParam = req.getParameter("introduced");
        String discontinuedParam = req.getParameter("discontinued");
        String companyIdParam = req.getParameter("companyId");

        String idParam = req.getParameter("id");
        int id = parseId(req, resp, errorMessages, idParam);
        System.out.println(idParam);
        if (id > 0) {
            req.setAttribute("id", id);
            if (computerName != null) {
                System.out.println("Un pc va etre modifié");
                System.out.println("Nom : " + computerName);
                CompanyDTO comp = getCompanyDTO(companyIdParam);

                ComputerDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(idParam, computerName)
                        .setDateIntroduction(introducedParam).setDateDiscontinuation(discontinuedParam)
                        .setEntreprise(comp).build();

                Computer com = null;
                try {
                    com = ComputerMapper.fromDTO(dtoComputer);
                } catch (MapperException mape) {
                    errorMessages.addAll(mape.getErrorList());
                }

                if (errorMessages.isEmpty()) {
                    try {
                        computerService.updateComputer(com);
                        req.setAttribute("headerMessage", "The computer has successfully been edited");
                    } catch (ComputerServiceException cse) {
                        errorMessages.add(cse.getMessage());
                    }
                }
            } else {
                Optional<Computer> optComp = computerService.getComputerById(id);
                if (optComp.isEmpty()) {
                    return redirectToDashboard();
                } else {
                    ComputerDTO c;
                    try {
                        c = ComputerMapper.toDTO(optComp.get());
                    } catch (MapperException e) {
                        logger.error(e.getMessage());
                        doGet(req, resp);
                        return;
                    }
                    req.setAttribute("computerName", c.getNom());
                    req.setAttribute("dateIntro", c.getDateIntroduction());
                    req.setAttribute("dateDiscont", c.getDateDiscontinuation());
                    req.setAttribute("companyId", c.getEntreprise() != null ? c.getEntreprise().getId() : 0);
                }
            }
            List<Company> companyList = companyService.getCompaniesList();
            req.setAttribute("companies", companyList);
            req.getRequestDispatcher("WEB-INF/views/editComputer.jsp").forward(req, resp);
        } else {
            doGet(req, resp);
        }

    }*/

    private CompanyDTO getCompanyDTO(long companyId) {
        CompanyDTO comp = null;
        Optional<Company> optComp = companyService.getCompanyById(companyId);
        if (optComp.isPresent()) {
            comp = new CompanyDTO(Long.toString(companyId), optComp.get().getNom());
        }
        return comp;
    }
}
