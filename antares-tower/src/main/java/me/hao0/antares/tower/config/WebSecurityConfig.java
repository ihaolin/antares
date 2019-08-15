package me.hao0.antares.tower.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // disable csrf
        http.csrf().disable();

        // uri match
        http.authorizeRequests()
                //.antMatchers("**/*.css", "**/*.js", "**/*.html").permitAll()
                //.antMatchers(ClientUris.CLIENT_API + "/**/*", ServerUris.SERVERS + "/**/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            @Value("${antares.user:admin}") String user,
            @Value("${antares.pass:{noop}admin}") String pass) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(user).password(pass).roles("ADMIN");
    }
}