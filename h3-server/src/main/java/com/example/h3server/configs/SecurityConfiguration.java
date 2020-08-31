package com.example.h3server.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // allows H2 throw Spring Security, TODO remove after migrating to mySQL
        web.ignoring().antMatchers("/h2-console/**");
    }
}
