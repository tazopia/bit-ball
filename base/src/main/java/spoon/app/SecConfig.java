package spoon.app;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import spoon.config.domain.Config;
import spoon.support.interceptor.AjaxInterceptorFilter;

import javax.servlet.http.HttpSessionListener;

@AllArgsConstructor
@Configuration
public class SecConfig extends WebSecurityConfigurerAdapter {

    private AjaxInterceptorFilter ajaxInterceptorFilter;

    private AuthenticationSuccessHandler loginSuccessHandler;

    private AuthenticationFailureHandler loginFailureHandler;

    private LogoutSuccessHandler logoutHandler;

    private UserDetailsService loginUserService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(loginUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/", "/block", "/error/**", "/fonts/**", "/js/**", "/css/**", "/images/**", "/sound/**")
                .antMatchers(Config.getPathJoin() + "/**")
                .antMatchers("/api/**")
                .antMatchers(Config.getPathAdmin())
                .antMatchers(Config.getPathSeller());
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .accessDeniedPage("/logout?error=3")

                .and()
                .headers()
                .frameOptions()
                .disable()

                .and()
                .formLogin()
                .loginPage("/")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutHandler)
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()

                .and()
                .authorizeRequests()
                .antMatchers("/images/**").permitAll()
                .antMatchers(Config.getPathAdmin() + "/system/**").access("hasRole('ROLE_GOD')")
                .antMatchers(Config.getPathAdmin() + "/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER') or hasRole('ROLE_GOD')")
                .antMatchers(Config.getPathSeller() + "/**").access("hasRole('ROLE_AGENCY1') or hasRole('ROLE_AGENCY2') or hasRole('ROLE_AGENCY3') or hasRole('ROLE_AGENCY4')")
                .antMatchers(Config.getPathSite() + "/**").access("hasRole('ROLE_USER') or hasRole('ROLE_DUMMY') or hasRole('ROLE_AGENCY1') or hasRole('ROLE_AGENCY2') or hasRole('ROLE_AGENCY3') or hasRole('ROLE_AGENCY4')")
                .anyRequest()
                .authenticated()

                .and()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/logout?error=2");

        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        http.addFilterBefore(filter, CsrfFilter.class)
                .addFilterAfter(ajaxInterceptorFilter, ExceptionTranslationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new SessionListener();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
