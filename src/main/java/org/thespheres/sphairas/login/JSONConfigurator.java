/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thespheres.sphairas.login;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author boris.heithecker@gmx.net
 */
@Provider
public class JSONConfigurator implements ContextResolver<Jsonb> {

    @Override
    public Jsonb getContext(Class<?> ignored) {
        final JsonbConfig cfg = new JsonbConfig()
                .withFormatting(true);
        return JsonbBuilder.create(cfg);
    }

}
