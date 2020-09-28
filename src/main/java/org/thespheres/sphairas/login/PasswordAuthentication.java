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
public interface PasswordAuthentication {

    public LoginResponse authenticate(final PasswordLoginRequest auth) throws Exception;

    public UserResponse getUserInfo(final String subject) throws Exception;

}
