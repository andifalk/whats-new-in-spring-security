package com.example.onetimetoken.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class MagicLinkGeneratedOneTimeTokenHandler implements OneTimeTokenGenerationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MagicLinkGeneratedOneTimeTokenHandler.class);

    private final MailSender mailSender;
    private final OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/api/demo");

    public MagicLinkGeneratedOneTimeTokenHandler(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    // This method is called when a One Time Token is generated successfully, sends the magic link via email, and redirects to the demo API.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException, ServletException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .path("/login/ott")
                .queryParam("token", oneTimeToken.getTokenValue());
        String magicLink = builder.toUriString();
        LOGGER.info("Magic link: {}", magicLink);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(oneTimeToken.getUsername());
        message.setFrom("secure@example.com");
        message.setSubject("Your Spring Security One Time Token");
        message.setText("Use the following link to sign in into the application: " + magicLink);
        LOGGER.info("Sending Magic link via EMail to {}", oneTimeToken.getUsername());
        this.mailSender.send(message);
        LOGGER.info("Sent Magic link via EMail to {}", oneTimeToken.getUsername());
        this.redirectHandler.handle(request, response, oneTimeToken);
    }
}
