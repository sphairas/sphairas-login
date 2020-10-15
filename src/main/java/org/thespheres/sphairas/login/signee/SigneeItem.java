/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login.signee;

import java.util.HashMap;
import java.util.Map;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.config.PropertyOrderStrategy;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@JsonbPropertyOrder(PropertyOrderStrategy.ANY)
public class SigneeItem {

    private String name;
    private final String account;
    private Map<String, String> properties = new HashMap<>();
    private String[] groups;

    @JsonbCreator
    public SigneeItem(@JsonbProperty("account") String account) {
        this.account = account;
    }

    SigneeItem(final SigneeEntity entity) {
        this(entity.getAccount());
        this.name = entity.getFullName();
        this.groups = entity.getGroups();
        this.properties.putAll(entity.getProperties());
    }

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    @JsonbProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonbProperty("account")
    public String getAccount() {
        return account;
    }

    @JsonbProperty("groups")
    public String[] getGroups() {
        return groups;
    }

    @JsonbProperty("groups")
    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    @JsonbProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonbProperty("properties")
    public void setProperties(final Map<String, String> map) {
        this.properties = map;
    }

}
