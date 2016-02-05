package com.centricient.service.admin;

import com.centricient.service.logging.WiretapAppender;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/wiretap")
public class WiretapResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getLogData() {
        return  WiretapAppender.getLogData().replace("\n", "<br>\n");
    }

    @POST
    @Path("/start")
    public String start() {
        WiretapAppender.setIsLoggingOn(true);
        return "Writetap logging started";
    }

    @POST
    @Path("/stop")
    public String stop() {
        WiretapAppender.setIsLoggingOn(false);
        return "Wiretap logging stopped";
    }
}
