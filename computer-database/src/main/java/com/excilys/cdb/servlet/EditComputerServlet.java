package com.excilys.cdb.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.CDBConfig;
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

@WebServlet("/EditComputer")
public class EditComputerServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 3345158907466408519L;
    private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);
    private static ComputerService computerService = CDBConfig.getContext().getBean("computerService",
            ComputerService.class);
    private static CompanyService companyService = CDBConfig.getContext().getBean("companyService",
            CompanyService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("./ListComputers");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                    doGet(req, resp);
                    return;
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

    }

    private CompanyDTO getCompanyDTO(String companyIdParam) {
        CompanyDTO comp = null;
        try {
            int idCompany = Integer.parseInt(companyIdParam);
            Optional<Company> optComp = companyService.getCompanyById(idCompany);
            if (optComp.isPresent()) {
                comp = new CompanyDTO(companyIdParam, CompanyMapper.toDTO(optComp.get()).getNom());
            }
        } catch (NumberFormatException | MapperException nfe) {
        }
        return comp;
    }

    private int parseId(HttpServletRequest req, HttpServletResponse resp, List<String> errorMessages, String idParam) throws ServletException, IOException {
        int id = 0;
        if (idParam != null) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException nfe) {
                logger.error("Invalid id received");
                errorMessages.add("Invalid id received");
            }
        }

        return id;
    }

}
