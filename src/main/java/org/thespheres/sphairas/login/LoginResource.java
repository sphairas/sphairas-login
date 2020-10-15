/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author boris.heithecker
 */
@Path("login")
@RequestScoped
@Transactional
public class LoginResource {

    static final String ISSUER = "GenJWT";
//    @Context
//    private UriInfo context;
    @Inject
    private PasswordAuthentication pwAuth;
    @Inject
    private JWTContext jwtContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse passwordLogin(final PasswordLoginRequest req, @Context HttpServletResponse resp) throws IOException {
        if (!req.isValid()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        final LoginResponse ret;
        try {
            ret = pwAuth.authenticate(req);
        } catch (final Exception ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        jwtContext.sign(req.getAccount(), ret);
        final String api = System.getenv("API_BASE_URL");
        ret.setApi(api);
        return ret;
    }

    //@RolesAllowed({"signee"})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserResponse getUserInfo(@HeaderParam("Authorization") final String auth, @Context HttpServletResponse resp) throws IOException {
        if (auth == null || auth.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        final String token;
        try {
            token = auth.substring("Bearer ".length());
        } catch (final IndexOutOfBoundsException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        final String subject;
        try {
            subject = jwtContext.parseToken(token, null);
        } catch (final JwtException | LoginException ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        final UserResponse ret;
        try {
            ret = pwAuth.getUserInfo(subject);
        } catch (final Exception ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        final String api = System.getenv("API_BASE_URL");
        ret.setApi(api);
        return ret;
    }

}
