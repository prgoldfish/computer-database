package com.excilys.cdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.springframework.security.core.GrantedAuthority;

@Entity(name = "authorities")
public class Authority implements GrantedAuthority {

    /**
     *
     */
    private static final long serialVersionUID = 1067255194429089150L;

    @Id
    //@OneToMany(targetEntity = User.class)
    @JoinColumn(name = "username")
    private String username;

    @Column(name = "authority")
    private String authority;

    public Authority() {
    }

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
