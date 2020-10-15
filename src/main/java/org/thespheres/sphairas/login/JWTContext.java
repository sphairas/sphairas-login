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
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.login.LoginException;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@Startup
@ApplicationScoped
public class JWTContext {

    public static final String P12_FILE = "server.p12";
    private Key signKey;
    private Key verifyKey;

    @PostConstruct
    public void initKeys() {
        String type = "";
        boolean success = false;
        final String secretsDir = System.getenv("SECRETS");
        final String secretKey = System.getenv("SECRET_KEY");
        if (secretKey != null) {
            final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            if (key != null) {
                this.signKey = key;
                this.verifyKey = key;
                type = "secret";
                success = true;
            }
        }
        if (!success && secretsDir != null) {
            final Path file = Paths.get(secretsDir).resolve(P12_FILE);
            if (Files.exists(file)) {
                try {
                    type = "rsa";
                    success = loadKeys(file, "changeit".toCharArray());
                } catch (final IOException ex) {
                    Logger.getLogger(JWTContext.class.getName()).log(Level.SEVERE, "Could not initialize RSA keys", ex);
                }
            }
        }
        Logger.getLogger(JWTContext.class.getName()).log(Level.INFO, "Keys initialized: {0}; type {1}", new Object[]{success, type});
    }

    private boolean loadKeys(final Path file, char[] pkcs12pw) throws IOException {
        final KeyStore ks;
        try {
            ks = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException ex) {
            throw new IOException(ex);
        }
        try (InputStream is = Files.newInputStream(file)) {
            ks.load(is, pkcs12pw);
        } catch (NoSuchAlgorithmException | CertificateException ex) {
            throw new IOException(ex);
        }
        final Enumeration<String> aliases;
        try {
            aliases = ks.aliases();
        } catch (KeyStoreException ex) {
            throw new IOException(ex);
        }
        PrivateKey privKey = null;
        PublicKey pubKey = null;
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            try {
                final KeyStore.Entry e = ks.getEntry(alias, new KeyStore.PasswordProtection(pkcs12pw));
                if (e instanceof KeyStore.PrivateKeyEntry) {
                    if (privKey != null || pubKey != null) {
                        throw new IOException("Found 2nd key");
                    }
                    final KeyStore.PrivateKeyEntry key = (KeyStore.PrivateKeyEntry) e;
                    privKey = key.getPrivateKey();
                    pubKey = key.getCertificate().getPublicKey();
                    //key.getCertificateChain();
                }
            } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException ex) {
                throw new IOException(ex);
            }
        }
        if (privKey != null && pubKey != null) {
            this.signKey = privKey;
            this.verifyKey = pubKey;
            return true;
        }
        return false;
    }

    public void sign(final String account, final LoginResponse ret) throws JwtException {
        final LocalDateTime now = LocalDateTime.now();
        final Date iat = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        final LocalDateTime expldt = now.plusMonths(1l);
        final Date exp = Date.from(expldt.atZone(ZoneId.systemDefault()).toInstant());

        final String jws = Jwts.builder()
                .setHeaderParam("kid", "app") //Key ID
                .setSubject(account) //sub
                //                .setIssuer(ISSUER)  //iss, not mandatory
                .setIssuedAt(iat) //iat
                .setExpiration(exp) //exp
                .claim("_couchdb.roles", List.of("signee"))
                .signWith(signKey).compact();
        ret.setJwt(jws);
        ret.setExp(exp.getTime());
    }

    public String parseToken(final String jwt, List<String> requireRoles) throws JwtException, LoginException {
        final Claims claims = Jwts.parserBuilder()
                //                    .requireIssuer(authToken)
                .setSigningKey(verifyKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        if (requireRoles != null) {
            final List<String> roles = claims.get("_couchdb.roles", List.class);
            if (!roles.containsAll(requireRoles)) {
                throw new LoginException();
            }
        }
        return claims.getSubject();
    }
}
