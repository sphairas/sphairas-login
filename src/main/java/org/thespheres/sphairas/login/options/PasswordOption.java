/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.options;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@JsonbPropertyOrder(PropertyOrderStrategy.ANY)
public class PasswordOption extends LoginOption {

    private String userHint;
    private String href;

    public PasswordOption() {
        super("password");
    }

    @JsonbProperty("user-hint")
    public String getUserHint() {
        return userHint;
    }

    @JsonbProperty("user-hint")
    public void setUserHint(final String userHint) {
        this.userHint = userHint;
    }

    @JsonbProperty("href")
    public String getHref() {
        return href;
    }

    @JsonbProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

}
