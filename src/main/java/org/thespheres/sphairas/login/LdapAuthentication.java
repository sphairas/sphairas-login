/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author boris.heithecker@gmx.net
 */
public class LdapAuthentication implements PasswordAuthentication {

    public static final String ATTR_USERDB = "labeledURI";
    public static final String LABEL_USERDB = "[userdb]";
    private final String ldap;
    private final String searchPath;
    private final String searchUser;
    private final String searchPassword;

    public LdapAuthentication(final String ldap, final String searchPath, final String searchUser, final String searchPassword) {
        this.ldap = ldap;
        this.searchPath = searchPath;
        this.searchUser = searchUser;
        this.searchPassword = searchPassword;
    }

    @Override
    public LoginResponse authenticate(final PasswordLoginRequest req) throws Exception {
        final String account = req.getAccount();
        final String password = req.getPassword();
        final LoginResponse ret = new LoginResponse(account);
        final Hashtable<String, Object> env = new Hashtable<>();

        final String dn = "uid=" + account + "," + searchPath;
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);

        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldap);
        final LdapContext ctx = new InitialLdapContext(env, null);
        final Attributes attr = ctx.getAttributes(dn);
        propulateResult(attr, ret, dn);
        return ret;
    }

    @Override
    public UserResponse getUserInfo(final String account) throws Exception {
        final UserResponse ret = new UserResponse(account);
        final Hashtable<String, Object> env = new Hashtable<>();

        final String dn = "uid=" + account + "," + searchPath;
        env.put(Context.SECURITY_PRINCIPAL, this.searchUser);
        env.put(Context.SECURITY_CREDENTIALS, this.searchPassword);

        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldap);

        final SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.OBJECT_SCOPE);
        controls.setTimeLimit(30000);
        final LdapContext ctx = new InitialLdapContext(env, null);
        ctx.setRequestControls(null);
        final NamingEnumeration<?> ne = ctx.search(dn, "(objectclass=person)", controls);
        while (ne.hasMore()) {
            final SearchResult res = (SearchResult) ne.next();
            final Attributes attr = res.getAttributes();
            propulateResult(attr, ret, dn);
        }
        ne.close();
        return ret;
    }

    protected void propulateResult(final Attributes attr, final UserResponse ret, final String dn) throws NamingException {
        final NamingEnumeration<? extends Attribute> ne = attr.getAll();
        while (ne.hasMore()) {
            final Attribute next = ne.next();
            if (ATTR_USERDB.equals(next.getID())) {
                final String value = (String) next.get();
                final String[] arr = value.split(" ");
                if (arr.length == 2 && LABEL_USERDB.equals(arr[1])) {
                    if (ret.getDb() == null) {
                        ret.setDb(arr[0]);
                    } else {
                        Logger.getLogger(LdapAuthentication.class.getCanonicalName()).log(Level.SEVERE, "Multiple labeledURI [userdb] entries for {0}", dn);
                    }
                }
            } else if ("cn".equals(next.getID())) {
                final String value = (String) next.get();
                ret.setName(value);
            }
        }
        ne.close();
    }
}
