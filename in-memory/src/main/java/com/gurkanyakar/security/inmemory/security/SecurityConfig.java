package com.gurkanyakar.security.inmemory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  //configuration classı oldugunu belirttik
@EnableWebSecurity //security filter chaini uygulamak icin acıyoruz
@EnableMethodSecurity // path yerine direkt olarak controllerin adresini verirsek ordaki methodlarin guvenligini saglar
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){ //bcrypt sha1 kullanır, passwordEncoder ise base64 kullanır
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(){
        UserDetails user1 = User.builder()
                .username("gürkan")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("testuser")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1,admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception{
        security
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x -> x.requestMatchers("/public/**","/auth/**").permitAll()) //permit all hepsine izin ver
                .authorizeHttpRequests(x -> x.anyRequest().authenticated()) // bunlar dışında gelen isteği authenticated et    --> üstteki satırlar yerleri değişirse herşey auth olur sıra önemli
                .httpBasic(Customizer.withDefaults());

        return security.build();
    }

}
