package com.excilys.cdb.persistence;

import java.util.stream.Collectors;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Authority;
import com.excilys.cdb.model.User;

public class UserDAO implements UserDetailsManager {

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
        cq.select(root).where(cb.equal(root.get("name"), username));
        try {
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUser(UserDetails user) {
        String username = user.getUsername();
        User u = new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.getAuthorities().stream()
                .map(ga -> new Authority(username, ga.getAuthority())).collect(Collectors.toList()));
        em.persist(u);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean userExists(String username) {
        // TODO Auto-generated method stub
        return false;
    }

}
