package com.sheryians.major.configuration;

import com.sheryians.major.model.Role;
import com.sheryians.major.model.User;
import com.sheryians.major.repository.RoleRespository;
import com.sheryians.major.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GoogleOAuth2SucessHandler implements AuthenticationSuccessHandler {
    @Autowired
    RoleRespository roleRespository;

    @Autowired
    UserRepository userRepository;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = getStringAttribute(token, "email");

        if(email != null){
            if(userRepository.findUserByEmail(email).isPresent()){

            }
            else{
                User user = new User();
                user.setFirstName(getStringAttribute(token, "given_name"));
                user.setLastName(getStringAttribute(token, "family_name"));
                user.setEmail(email);

                List<Role> roles = new ArrayList<>();
                Role defaultRole = roleRespository.findById(2).orElse(null);

                if (defaultRole != null) {
                    roles.add(defaultRole);
                    user.setRoles(roles);
                } else {
                    System.err.println("Role with ID 2 not found!");
                }
                userRepository.save(user);
            }
        }
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");
    }

    private String getStringAttribute(OAuth2AuthenticationToken token, String attributeName) {
        Object attributeValue = token.getPrincipal().getAttributes().get(attributeName);
        return attributeValue != null ? attributeValue.toString() : null;
    }
}
