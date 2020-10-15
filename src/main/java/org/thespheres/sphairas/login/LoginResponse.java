/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@JsonbPropertyOrder(PropertyOrderStrategy.ANY)
public class LoginResponse extends UserResponse {

    private String jwt;
    private long exp;

    @JsonbCreator
    public LoginResponse(@JsonbProperty("user") final String user) {
        super(user);
    }

    @JsonbProperty("jwt")
    public String getJwt() {
        return jwt;
    }

    @JsonbProperty("jwt")
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @JsonbProperty("exp")
    public long getExp() {
        return exp;
    }

    @JsonbProperty("exp")
    public void setExp(long exp) {
        this.exp = exp;
    }

}
