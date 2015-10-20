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
import net.es.maddash.madalert.MeshDiff;
import net.es.maddash.madalert.Problem;
import net.es.maddash.madalert.Report;
import net.es.maddash.madalert.Rule;

/**
 * REST Web Service for diff
 *
 * @author carcassi
 */
@Path("diff")
public class DiffResource {

    @Context
    private UriInfo context;

    /**
     * Retrieves representation of an instance of net.es.maddash.mavenrest.ReportResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String generateReport(@QueryParam("mesh1") String url1, @QueryParam("mesh2") String url2) {
        Client client = null;
        try {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            WebTarget target1 = client.target(url1);
            WebTarget target2 = client.target(url2);
            Mesh mesh1 = Mesh.from(Json.createReader(target1.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(InputStream.class)).readObject(), url1);
            Mesh mesh2 = Mesh.from(Json.createReader(target2.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(InputStream.class)).readObject(), url2);
            Mesh diff = MeshDiff.diff(mesh1, mesh2);
            return diff.toJson().toString();
        } catch(Exception ex) {
            return "{ \"" + ex.getMessage() + "\" }";
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
