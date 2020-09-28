/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
        //        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        KeyPair kp = Keys.keyPairFor(SignatureAlgorithm.RS512);
//        byte[] encoded = key.getEncoded();
//        String secret = Encoders.BASE64.encode(encoded);
        final Key key = getkey();

        final LocalDateTime now = LocalDateTime.now();
        final Date iat = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        final LocalDateTime expldt = now.plusMonths(1l);
        final Date exp = Date.from(expldt.atZone(ZoneId.systemDefault()).toInstant());

        final String jws = Jwts.builder()
                .setHeaderParam("kid", "app") //Key ID
                .setSubject(req.getAccount()) //sub
                //                .setIssuer(ISSUER)  //iss, not mandatory
                .setIssuedAt(iat) //iat
                .setExpiration(exp) //exp
                .claim("_couchdb.roles", List.of("signee"))
                .signWith(key).compact();
        ret.setJwt(jws);
        ret.setExp(exp.getTime());
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
            subject = parseToken(token);
        } catch (final JwtException ex) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
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

    private static String parseToken(final String jwt) throws JwtException {
        Key key = getkey();
        final Claims claims = Jwts.parserBuilder()
                //                    .requireIssuer(authToken)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }

    private static Key getkey() throws WeakKeyException, DecodingException {
        final String sk = System.getenv("SECRET_KEY");
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(sk));
    }
}
