package com.excilys.cdb.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.User;

@Repository
public class UserDAO implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    private EntityManager em;

    @Autowired
    public UserDAO(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root).where(cb.equal(root.get("username"), username));
        try {
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }
}
