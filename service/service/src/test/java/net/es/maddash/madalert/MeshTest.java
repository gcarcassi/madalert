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

/**
 *
 * @author carcassi
 */
public class MeshTest {

    @Test
    public void testGetSites() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allWell.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            assertThat(mesh.getSites(), equalTo(Arrays.asList("CERN-PROD LAT", "FZK-LCG2 LAT", "IN2P3-CC LAT", "INFN-T1 LAT", "KR-KISTI-GSDC-01 LAT", "NDGF-T1 LAT", "RAL-LCG2 LAT", "RRC-KI-T1 LAT", "SARA-MATRIX LAT", "TRIUMF-LCG2 LAT", "Taiwan-LCG2 LAT", "US-FNAL LT", "lhcperfmon-bnl", "pic LAT")));
        }
    }

}
