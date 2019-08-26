package lt.bit.java2.springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}

@Configuration
class MVCConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error").setViewName("error");
//        registry.addViewController("/").setViewName("index");
//        registry.addViewController("/admin").setViewName("admin");
    }
}

@Configuration
class Config {

    @Bean
    public UserService userService() {
        return new UserService("Jo didenybe");
    }
}

@Controller
class Hello {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/hello")
    public String hello(ModelMap map) {
        map.addAttribute("name", "Jonas");
        return "hello";
    }

}


class UserService {
    final private String title;

    UserService(String title) {
        this.title = title;
    }

    public String getName() {
        return "Jonaitis";
    }
}

@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
//                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .and()
                .logout();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        List<UserDetails> users = Arrays.asList(
                User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build(),
                User.withDefaultPasswordEncoder().username("admin").password("admin").roles("USER", "ADMIN").build()
        );
        return new InMemoryUserDetailsManager(users);
    }
}
