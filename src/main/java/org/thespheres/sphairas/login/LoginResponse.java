/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

/**
 *
 * @author boris.heithecker@gmx.net
 */
public class LoginResponse extends UserResponse {

    private String jwt;
    private long exp;

    public LoginResponse() {
    }

    public LoginResponse(final String user) {
        super(user);
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

}
