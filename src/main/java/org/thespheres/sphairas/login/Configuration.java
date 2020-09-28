/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.thespheres.sphairas.login.iserv.IServAuthentication;
import org.thespheres.sphairas.login.signee.SigneeManager;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@ApplicationScoped
public class Configuration {

    @Produces
    public PasswordAuthentication createPasswordAuthentication(final SigneeManager manager) {
        final String ldapUrl = System.getenv("LDAP_URL");
        final String ldapPath = System.getenv("LDAP_SEARCH_PATH");
        final String ldapSearchUser = System.getenv("LDAP_SEARCH_USER");
        final String ldapSearchUserPassword = System.getenv("LDAP_SEARCH_USER_PASSWORD");
        final String iservHost = System.getenv("ISERV_IMAP_HOST");
        final String iservPort = System.getenv("ISERV_IMAP_PORT");
        if (ldapUrl != null && ldapPath != null && ldapSearchUser != null && ldapSearchUserPassword != null) {
            return new LdapAuthentication(ldapUrl, ldapPath, ldapSearchUser, ldapSearchUserPassword);
        } else if (iservHost != null) {
            final int port = iservPort != null ? Integer.parseInt(iservPort) : IServAuthentication.ISERV_IMAP_DEFAULT_PORT;
            return new IServAuthentication(iservHost, port, manager);
        }
        throw new IllegalStateException();
    }

}
