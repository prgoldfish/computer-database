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
import com.excilys.cdb.springconfig.CDBConfig;

@WebServlet("/AddComputer")
public class AddComputerServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -6234124633063870193L;
    //private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);
    ComputerService computerService = CDBConfig.getContext().getBean(ComputerService.class);
    CompanyService companyService = CDBConfig.getContext().getBean(CompanyService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String headerMessage = (String) req.getAttribute("headerMessage");
        if (headerMessage == null || headerMessage.isEmpty()) {
            List<Company> companyList = companyService.getCompaniesList();
            req.setAttribute("companies", companyList);
        }
        req.getRequestDispatcher("WEB-INF/views/addComputer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String computerName = req.getParameter("computerName");
        String introducedParam = req.getParameter("introduced");
        String discontinuedParam = req.getParameter("discontinued");
        String companyIdParam = req.getParameter("companyId");

        List<String> errorMessages = new ArrayList<>();

        CompanyDTO comp = null;
        try {
            int id = Integer.parseInt(companyIdParam);
            Optional<Company> optComp = companyService.getCompanyById(id);
            if (optComp.isPresent()) {
                comp = new CompanyDTO(companyIdParam, CompanyMapper.toDTO(optComp.get()).getNom());
            }
        } catch (NumberFormatException | MapperException nfe) {
        }

        ComputerDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(Long.toString(computerService.getMaxId() + 1),
                computerName).setDateIntroduction(introducedParam).setDateDiscontinuation(discontinuedParam)
                        .setEntreprise(comp).build();

        Computer com = null;
        try {
            com = ComputerMapper.fromDTO(dtoComputer);
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }

        if (errorMessages.isEmpty()) {
            try {
                computerService.addNewComputer(com);
                req.setAttribute("headerMessage", "The computer has successfully been added to the database");
            } catch (ComputerServiceException cse) {
                errorMessages.add(cse.getMessage());
            }
        }
        req.setAttribute("errors", errorMessages);
        doGet(req, resp);

    }
}
