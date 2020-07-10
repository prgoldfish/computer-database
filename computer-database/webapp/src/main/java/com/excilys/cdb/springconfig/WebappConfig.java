package com.excilys.cdb.springconfig;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.excilys.cdb.persistence.UserDAO;

@EnableWebMvc
@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = { "com.excilys.cdb.controller", "com.excilys.cdb.springconfig" })
public class WebappConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    DigestAuthenticationFilter digestAuthenticationFilter;

    @Autowired
    DigestAuthenticationEntryPoint entryPoint;

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.FRANCE);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /*    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager
                .createUser(User.withUsername("cdbAdmin").password("password").roles("ADMIN").build());
        inMemoryUserDetailsManager.createUser(User.withUsername("cdbUser").password("password").roles("USER").build());
        return inMemoryUserDetailsManager;
    }
    */
    @Bean
    DigestAuthenticationEntryPoint digestEntryPoint() {
        DigestAuthenticationEntryPoint bauth = new DigestAuthenticationEntryPoint();
        bauth.setRealmName("Digest WF Realm");
        bauth.setKey("MySecureKey");
        return bauth;
    }

    @Bean
    DigestAuthenticationFilter digestAuthenticationFilter(UserDAO userDetailsService) throws Exception {
        DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
        digestAuthenticationFilter.setUserDetailsService(userDetailsService);
        digestAuthenticationFilter.setAuthenticationEntryPoint(digestEntryPoint());
        return digestAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/AddComputer", "/EditComputer").hasRole("ADMIN").and()
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint)).addFilter(digestAuthenticationFilter)
                .logout().logoutUrl("logout").logoutSuccessUrl("/");
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/");
        registry.addResourceHandler("/images/**").addResourceLocations("/images/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/WEB-INF/views/403.jsp", "403");
        registry.addRedirectViewController("/WEB-INF/views/404.jsp", "404");
        registry.addRedirectViewController("/WEB-INF/views/500.jsp", "500");
    }

}
