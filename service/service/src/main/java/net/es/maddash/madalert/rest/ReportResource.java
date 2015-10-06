/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert.rest;

import java.io.InputStream;
import javax.json.Json;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import net.es.maddash.madalert.Madalert;
import net.es.maddash.madalert.Mesh;
import net.es.maddash.madalert.Problem;
import net.es.maddash.madalert.Report;
import net.es.maddash.madalert.Rule;

/**
 * REST Web Service
 *
 * @author carcassi
 */
@Path("report")
public class ReportResource {

    @Context
    private UriInfo context;

    /**
     * Retrieves representation of an instance of net.es.maddash.mavenrest.ReportResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String generateReport(@QueryParam("json") String jsonUrl) {
        WebTarget webTarget;
        Client client = null;
        try {
            long start = System.nanoTime();
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(jsonUrl);
            long phase1 = System.nanoTime();
            WebTarget resource = webTarget;
//            String output = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
            Mesh mesh = Mesh.from(Json.createReader(resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(InputStream.class)).readObject(), jsonUrl);
            long phase2 = System.nanoTime();
            Report report = Madalert.defaultRule().createReport(mesh);
            long phase3 = System.nanoTime();
            String output = report.toJson().toString();
            long end = System.nanoTime();
            System.out.println("TIMING: " + (phase1 - start) + " - " + (phase2 - phase1) + " - " + (phase3 - phase2) + " - " + (end - phase3));
            return output;
        } catch(Exception ex) {
            return "{ \"" + ex.getMessage() + "\" }";
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
