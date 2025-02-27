package com.shodhAI.ShodhAI.Configuration;

import com.shodhAI.ShodhAI.Component.JwtAuthenticationFilter;
import com.shodhAI.ShodhAI.Service.StudentService;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
public class SecurityConfig {

    private final StudentService studentService;

    public SecurityConfig(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    private JwtAuthenticationEntryPoint point;

//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/register").permitAll()  // Allow public access to login & register
                        .anyRequest().authenticated()  // Require authentication for all other routes
                )
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  // Add JWT filter after the login route

        return http.build();
    }*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.
                csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/content/get-all-content-type", "/upload").authenticated()
                                        .requestMatchers("/auth/login-with-username-password", "/student/add", "/faculty/add", "/")
                                        .permitAll()
                                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/register").permitAll()  // Allow public access to login & register
                        .anyRequest().authenticated()  // Require authentication for all other routes
                )
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add the filter before the authentication filter

//                // If you need form-based login as well, uncomment and customize the following line
//                .formLogin()
//                .loginPage("api/v1/auth/login")  // Custom login page
//                .permitAll();  // Allow all users to access the login page

        return http.build();
    }*/

    // Configure the AuthenticationManagerBuilder to use StudentService as the UserDetailsService
//    @Bean
//    public AuthenticationManagerBuilder authenticationManagerBuilder(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        authenticationManagerBuilder
//                .userDetailsService(studentService)  // Using StudentService as the custom UserDetailsService
//                .passwordEncoder(passwordEncoder()); // Password encoder for password encryption
//
//        return authenticationManagerBuilder;
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Disable CSRF for all endpoints
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // Allow access to all endpoints without authentication
//                )
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()));  // Apply global CORS config
//        return http.build();
//    }
//
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Instead of "*", specify the exact origins you want to allow
        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // Add your client URL here
        corsConfiguration.addAllowedOrigin("https://release.shodhlab.ai"); // If needed for another origin

        corsConfiguration.addAllowedMethod("*"); // Allow all methods (GET, POST, etc.)
        corsConfiguration.addAllowedHeader("*"); // Allow all headers
        corsConfiguration.setAllowCredentials(true); // Allow cookies/credentials if necessary
        corsConfiguration.setMaxAge(3600L); // Cache preflight response for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // Apply CORS configuration globally
        return source;
    }

    @Bean
    public Filter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
