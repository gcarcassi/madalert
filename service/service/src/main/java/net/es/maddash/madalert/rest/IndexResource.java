/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author carcassi
 */
@Path("index")
public class IndexResource {

    @GET
    @Produces("application/json")
    public String generateReport(@QueryParam("site") String site) {
        if (site == null) {
            return "{ \"Site parameter must be specified\" }";
        }
        try {
            URI uri = new URI("http://" + site + "/maddash/grids");
            JsonObject jsonObject = Json.createReader(uri.toURL().openStream()).readObject();
            return jsonObject.toString();
        } catch (URISyntaxException | MalformedURLException ex) {
            return "{ \"JSON parameter is not a valid URI (" + ex.getMessage() + ")\" }";
        } catch (RuntimeException | IOException ex) {
            return "{ \"Error reading URI (" + ex.getMessage() + ")\" }";
        }
    }
}
