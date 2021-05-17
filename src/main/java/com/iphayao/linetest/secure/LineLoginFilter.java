package com.iphayao.linetest.secure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iphayao.linetest.util.CommonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

public class LineLoginFilter extends GenericFilterBean {
    private RememberMeServices rememberMeServices = new NullRememberMeServices();

    private String clientId;
    private String redirectUri;

    public LineLoginFilter(String clientId, String redirectUri) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        AccessToken accessToken = (AccessToken) httpRequest.getSession().getAttribute("ACCESS_TOKEN");

        if(accessToken == null) {
            String callbackUrl = URLEncoder.encode(redirectUri, "UTF-8");
            String loginUrl = "https://access.line.me/oauth2/v2.1/authorize?response_type=code&"
                    + "client_id=" + clientId + "&"
                    + "redirect_uri=" + callbackUrl + "&"
                    + "state=" + CommonUtils.getToken() + "&"
                    + "scope=openid";
            httpRequest.getSession().setAttribute("REQUEST_URL", httpRequest.getRequestURI());
            httpResponse.sendRedirect(loginUrl);
            return;
        } else {

            DecodedJWT jwt = JWT.decode(Objects.requireNonNull(accessToken).getIdToken());
            Authentication authToken = IdToken.builder()
                    .name(jwt.getClaim("name").asString())
                    .userId(jwt.getClaim("sub").asString())
                    .nonce(jwt.getClaim("nonce").asString())
                    .picture(jwt.getClaim("picture").asString())
                    .build();

            SecurityContextHolder.getContext().setAuthentication(authToken);
            rememberMeServices.loginSuccess(httpRequest, httpResponse, authToken);
        }

        chain.doFilter(request, response);
    }
}
