package com.iphayao.linetest.secure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecureConfig extends WebSecurityConfigurerAdapter {
    @Value("${line.login.client-id}")
    private String clientId;

    @Value("${line.login.redirect_uri}")
    private String redirectUri;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAt(lineLoginFilter(), BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/auth", "/callback");
    }

    private LineLoginFilter lineLoginFilter() {
        return new LineLoginFilter(clientId, redirectUri);
    }
}
