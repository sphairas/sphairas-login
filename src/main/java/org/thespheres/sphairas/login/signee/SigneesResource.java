/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.signee;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.thespheres.betula.document.Signee;
import org.thespheres.sphairas.login.JWTContext;

/**
 * REST Web Service
 *
 * @author boris.heithecker@gmx.net
 */
@Path("/signees")
@RequestScoped
public class SigneesResource {

    @Context
    private UriInfo context;
    @Inject
    private SigneeManager manager;
    @Inject
    private JWTContext jwtContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SigneeItem[] getSignees(@HeaderParam("Authorization") final String auth, @Context final HttpServletResponse resp) throws IOException {
        try {
            authorize(auth);
        } catch (final LoginException ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        return manager.findAllSigneeItems();
    }

    @Path("{signee}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SigneeItem getSignee(@PathParam("signee") final String signee, @HeaderParam("Authorization") final String auth, @Context final HttpServletResponse resp) throws IOException {
        try {
            authorize(auth);
        } catch (final LoginException ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        final Signee sig = Signee.parse(signee);
        if (Signee.isNull(sig)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return manager.findSigneeItem(sig);
    }

    @Path("{signee}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putSignee(@PathParam("signee") final String signee, @HeaderParam("Authorization") final String auth, @Context final HttpServletResponse resp, final SigneeItem item) throws IOException {
        try {
            authorize(auth);
        } catch (final LoginException ex) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        final Signee sig = Signee.parse(signee);
        if (Signee.isNull(sig)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (item.getGroups() != null && Arrays.stream(item.getGroups()).map(String::strip).anyMatch("signees"::equals)) { //"signees" is implied and must not be specified
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        manager.updateSigneeItem(sig, item);
        return Response
                .ok()
                .build();
    }

    void authorize(final String auth) throws LoginException {
        if (auth == null || auth.isBlank()) {
            throw new LoginException();
        }
        final String token;
        try {
            token = auth.substring("Bearer ".length());
        } catch (final IndexOutOfBoundsException e) {
            throw new LoginException();
        }
        try {
            jwtContext.parseToken(token, List.of("admin"));
        } catch (final JwtException | LoginException ex) {
            throw new LoginException(ex.getMessage());
        }
    }
}
