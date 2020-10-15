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
public class UserResponse {

    protected String db;
    protected String api;
    protected String user;
    protected String name;

    @JsonbCreator
    public UserResponse(@JsonbProperty("user") final String user) {
        this.user = user;
    }

    @JsonbProperty("user")
    public String getUser() {
        return user;
    }

    @JsonbProperty("db")
    public String getDb() {
        return db;
    }

    @JsonbProperty("db")
    public void setDb(final String db) {
        this.db = db;
    }

    @JsonbProperty("api")
    public String getApi() {
        return api;
    }

    @JsonbProperty("api")
    public void setApi(final String api) {
        this.api = api;
    }

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    @JsonbProperty("name")
    public void setName(String name) {
        this.name = name;
    }

}
