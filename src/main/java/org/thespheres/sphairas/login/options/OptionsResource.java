package org.thespheres.sphairas.login.options;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author
 */
@Path("options")
public class OptionsResource {

//    @Context
//    private UriInfo context;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)  
    @Produces(MediaType.APPLICATION_JSON)
    public PasswordOption[] options(@QueryParam("login_key") final String key, @Context HttpServletResponse resp) throws IOException {
        String loginKey = System.getenv("LOGIN_KEY");
        if (key == null || loginKey == null || !Objects.equals(key, loginKey)) {
//            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//            return null;
        }
        final String hint = "Das sphairas-Passwort";
        final PasswordOption option = new PasswordOption();
        option.setUserHint(hint);
        return new PasswordOption[]{option};
    }
}
