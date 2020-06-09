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
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.dto.CompanyDTO;
import com.excilys.cdb.dto.ComputerDTO;
import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.MapperException;
import com.excilys.cdb.mapper.CompanyMapper;
import com.excilys.cdb.mapper.ComputerMapper;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;

@WebServlet("/AddComputer")
public class AddComputerServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -6234124633063870193L;
    private static final Logger logger = LoggerFactory.getLogger(AddComputerServlet.class);

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!Boolean.TRUE.equals(req.getAttribute("addedcomputer")))
        {
            HttpSession session = req.getSession();
            CompanyDAO companyDao = (CompanyDAO) session.getAttribute("companydao");
            if(companyDao == null)
            {
                companyDao = new CompanyDAO();
                session.setAttribute("companydao", companyDao);
            }
            CompanyService companyService = (CompanyService) session.getAttribute("companyservice");
            if(companyService == null)
            {
                companyService = new CompanyService(companyDao);
                session.setAttribute("companyservice", companyService);
            }
            
            List<Company> companyList = companyService.getCompaniesList();
            req.setAttribute("companies", companyList);
        }
        req.getRequestDispatcher("WEB-INF/views/addComputer.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(); 
        String computerName = req.getParameter("computerName");
        String introducedParam = req.getParameter("introduced");
        String discontinuedParam = req.getParameter("discontinued");
        String companyIdParam = req.getParameter("companyId");
        
        ComputerDAO computerDao = (ComputerDAO) session.getAttribute("computerdao");
        if(computerDao == null)
        {
            computerDao = new ComputerDAO();
            session.setAttribute("computerdao", computerDao);
        }
        ComputerService computerService = (ComputerService) session.getAttribute("computerservice");
        if(computerService == null)
        {
            computerService = new ComputerService(computerDao);
            session.setAttribute("computerservice", computerService);
        }
        
        CompanyDAO companyDao = (CompanyDAO) session.getAttribute("companydao");
        if(companyDao == null)
        {
            companyDao = new CompanyDAO();
            session.setAttribute("companydao", companyDao);
        }
        CompanyService companyService = (CompanyService) session.getAttribute("companyservice");
        if(companyService == null)
        {
            companyService = new CompanyService(companyDao);
            session.setAttribute("companyservice", companyService);
        }

        logger.debug(Boolean.toString(computerName.equals("")));
        logger.debug(introducedParam);
        logger.debug(discontinuedParam);
        logger.debug(companyIdParam);
        
        List<String> errorMessages = new ArrayList<>(); 
        
        CompanyDTO comp = null;
        try {
            int id = Integer.parseInt(companyIdParam);
            Optional<Company> optComp = companyService.getCompanyById(id);
            if(optComp.isPresent())
            {
                comp = new CompanyDTO(companyIdParam, CompanyMapper.toDTO(optComp.get()).getNom());
            }            
        } catch (NumberFormatException | MapperException nfe) {}
        
        ComputerDTO dtoComputer = new ComputerDTO.ComputerBuilderDTO(Long.toString(computerDao.getMaxId() + 1), computerName)
                                    .setDateIntroduction(introducedParam)
                                    .setDateDiscontinuation(discontinuedParam)
                                    .setEntreprise(comp)
                                    .build();
        
        Computer com = null;
        try {
            com = ComputerMapper.fromDTO(dtoComputer);
        } catch (MapperException mape) {
            errorMessages.addAll(mape.getErrorList());
        }
        
        if(errorMessages.isEmpty())
        {
            try
            {
                computerService.addNewComputer(com);
                req.setAttribute("addedcomputer", true);
            } catch (ComputerServiceException cse) {
                errorMessages.add(cse.getMessage());
            }
        }
        req.setAttribute("errors", errorMessages);
        doGet(req, resp);
        
    }
}
