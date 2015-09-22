/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

/**
 *
 * @author carcassi
 */
public class RuleTest {

    @Test
    public void allSites() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allWell.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            TestSet testSet = Rule.forAllSites();
            assertThat(testSet.match(mesh, Rule.matchStatus(0)), equalTo(true));
            assertThat(testSet.match(mesh, Rule.matchStatus(3)), equalTo(false));
        }
    }

    @Test
    public void site3Down() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site3Down.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            assertThat(Rule.forSite().site(3).match(mesh, Rule.matchStatus(3)), equalTo(true));
            assertThat(Rule.forSite().site(2).match(mesh, Rule.matchStatus(3)), equalTo(false));
        }
    }

    @Test
    public void site2CantTest() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site2CantTest.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            assertThat(Rule.forInitiatedBySite().site(2).match(mesh, Rule.matchStatus(3)), equalTo(true));
            assertThat(Rule.forInitiatedOnSite().site(2).match(mesh, Rule.matchStatus(3)), equalTo(false));
        }
    }

}
