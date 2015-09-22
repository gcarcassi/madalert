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
public class StatusMatcherTest {

    @Test
    public void matchStatus1() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site2CantTest.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            StatusMatcher matcher = StatusMatcher.matchStatus(3);
            StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
            assertThat(instance.isMatched(), equalTo(true));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
            assertThat(instance.isMatched(), equalTo(true));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 0);
            assertThat(instance.isMatched(), equalTo(false));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
            assertThat(instance.isMatched(), equalTo(false));
        }
    }

    @Test
    public void matchStatus2() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site2CantTest.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            StatusMatcher matcher = StatusMatcher.matchStatus(3, 0.7);
            StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
            assertThat(instance.isMatched(), equalTo(true));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
            assertThat(instance.isMatched(), equalTo(true));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 0);
            assertThat(instance.isMatched(), equalTo(false));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
            assertThat(instance.isMatched(), equalTo(false));
            instance.match(0, 1, Mesh.CellHalf.INITIATED_BY_ROW, 3);
            assertThat(instance.isMatched(), equalTo(true));
        }
    }

}
