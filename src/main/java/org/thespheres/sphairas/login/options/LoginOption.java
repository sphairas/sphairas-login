/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.options;

import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author boris.heithecker@gmx.net
 */
public abstract class LoginOption {

    private final String type;

    protected LoginOption(final String type) {
        this.type = type;
    }

    @JsonbProperty("type")
    public String getType() {
        return type;
    }
}
