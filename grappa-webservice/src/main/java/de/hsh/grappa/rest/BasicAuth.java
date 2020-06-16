package de.hsh.grappa.rest;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;


@Provider
public class BasicAuth implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(BasicAuth.class);
    private static final String AUTHENTICATION_SCHEME = "[B|b]asic";

    @Override
    public void filter(ContainerRequestContext containerRequest)
        throws IOException {
        var auth = containerRequest.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (auth == null || auth.isEmpty())
            throw new AuthenticationException(String.format
                ("Unauthorized access to resource '{}'.",
                    containerRequest.getUriInfo().getPath()));
        String encodedUserPassw = auth.get(0).replaceFirst
            (AUTHENTICATION_SCHEME + " ", "");
        String userPassw = new String(Base64.getDecoder().decode(encodedUserPassw.getBytes()));
        var token = new StringTokenizer(userPassw, ":");
        String user = token.nextToken();
        String passw = token.nextToken();
        var lms = GrappaServlet.CONFIG.getLms().stream()
            .filter(l -> l.getId().equals(user)
                && l.getPassword_hash().equals(passw)).findFirst();
        if (!lms.isPresent())
            throw new AuthenticationException("Invalid username or password.");
        containerRequest.setProperty("logged_user", lms.get());
    }
}
