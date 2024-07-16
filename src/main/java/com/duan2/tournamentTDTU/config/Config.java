package com.duan2.tournamentTDTU.config;

import com.duan2.tournamentTDTU.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Config {

    @Autowired
    private AccountService accountService;

    private static final String[] WHITELIST ={
            "/",
            "/login",
            "/index",
            "/home",
            "/user/**",
            "/avatars/**",
            "/list_team",
            "/team/**",
            "/match/**",
            "/match_direct/**"
    };

    private static final RequestMatcher CUSTOM_REQUEST_MATCHER = new OrRequestMatcher(
            new RegexRequestMatcher("^/\\d{4}$", "GET"),
            new RegexRequestMatcher("^/index/\\d{4}$", "GET"),
            new RegexRequestMatcher("^/home/\\d{4}$", "GET"),
            new RegexRequestMatcher("^/\\d{4}-\\d{4}(/.*)?$", "GET")
    );

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(configurer ->
                configurer
                    .requestMatchers(WHITELIST).permitAll()
                    .requestMatchers(CUSTOM_REQUEST_MATCHER).permitAll()
                    .requestMatchers("/create_tournament").hasRole("MANAGER")
                    .requestMatchers("/create_schedule").hasRole("MANAGER")
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                    .requestMatchers("/data/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form ->
                form
                    .loginPage("/login")
                    .usernameParameter("ID")
                    .passwordParameter("password")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/",true)
                    .failureUrl("/login?error")
                    .permitAll()
                )
                .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixationConfigurer -> sessionFixationConfigurer.migrateSession())
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/index")
                        .maxSessionsPreventsLogin(false)
                );

        http.addFilterBefore(roleUpdateFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RoleUpdateFilter roleUpdateFilter() {
        return new RoleUpdateFilter();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
