/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.options;

/**
 *
 * @author boris.heithecker@gmx.net
 */
public class PasswordOption extends LoginOption {

    private String userHint;
    private String href;

    public PasswordOption() {
        super("password");
    }

    public String getUserHint() {
        return userHint;
    }

    public void setUserHint(String userHint) {
        this.userHint = userHint;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
