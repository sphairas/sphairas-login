/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.iserv;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import org.apache.commons.net.imap.AuthenticatingIMAPClient;
import org.apache.commons.net.imap.IMAPSClient;
import org.thespheres.sphairas.login.LoginResponse;
import org.thespheres.sphairas.login.PasswordAuthentication;
import org.thespheres.sphairas.login.PasswordLoginRequest;
import org.thespheres.sphairas.login.UserResponse;
import org.thespheres.sphairas.login.signee.SigneeManager;

/**
 *
 * @author boris.heithecker@gmx.net
 */
public class IServAuthentication implements PasswordAuthentication {

    public static final int ISERV_IMAP_DEFAULT_PORT = 993;

    private final String host;
    private final int port;
    private final SigneeManager manager;

    public IServAuthentication(final String host, final int port, final SigneeManager manager) {
        this.host = host;
        this.port = port;
        this.manager = manager;
    }

    @Override
    public LoginResponse authenticate(final PasswordLoginRequest auth) throws LoginException {
        final String account = auth.getAccount();
        final String[] grpList = manager.getSigneeGroups(account);
        if (grpList == null || grpList.length == 0) {
            throw new LoginException();
        }

        boolean success = false;
        final AuthenticatingIMAPClient cl;
        try {
            cl = new AuthenticatingIMAPClient(IMAPSClient.DEFAULT_PROTOCOL, true);
            cl.setEndpointCheckingEnabled(true);
            cl.connect(host, port);
        } catch (IOException ex) {
            Logger.getLogger(IServAuthentication.class.getCanonicalName()).log(Level.WARNING, "Could not establish connection to iserv.", ex);
            throw new LoginException();
        }

        try {
            success = cl.authenticate(AuthenticatingIMAPClient.AUTH_METHOD.PLAIN, account, auth.getPassword());
            cl.logout();
        } catch (IOException ex) {
            Logger.getLogger(IServAuthentication.class.getCanonicalName()).log(Level.WARNING, "Failed to log in on iserv.", ex);
            final LoginException lex = new LoginException();
            lex.initCause(ex);
            throw lex;
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException ex) {
            Logger.getLogger(IServAuthentication.class.getName()).log(Level.SEVERE, null, ex);
            final LoginException lex = new LoginException();
            lex.initCause(ex);
            throw lex;
        } finally {
            try {
                cl.disconnect();
            } catch (IOException ex) {
            }
        }
        if (!success) {
            throw new LoginException();
        }
        final LoginResponse ret = new LoginResponse(account);
        populateResponse(account, ret);
        return ret;
    }

    @Override
    public UserResponse getUserInfo(final String account) throws Exception {
        final UserResponse ret = new UserResponse(account);
        populateResponse(account, ret);
        return ret;
    }

    void populateResponse(final String account, final UserResponse ret) {
        final String db = manager.getSigneeProperties(account).get("couchdb");
        ret.setDb(db);
        final String name = manager.getSigneeName(account);
        ret.setName(name);
    }
}
