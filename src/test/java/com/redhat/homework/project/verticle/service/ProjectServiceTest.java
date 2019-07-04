package com.redhat.homework.project.verticle.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.homework.project.verticle.service.ProjectService;
import com.redhat.homework.project.verticle.service.ProjectServiceImpl;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceTest extends MongoTestBase {

    private Vertx vertx;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "projects", async, context);
        async.await(10000);
    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }

    @Test
    public void testGetProjects(TestContext context) throws Exception {
        String itemId = "1234";
        JsonObject json1 = new JsonObject()
                .put("projectId", itemId)
                .put("ownerFirstName", "John")
                .put("ownerLastName", "Doe")
                .put("ownerEmailAddress", "jdoe@example.com")
                .put("projectTitle", "Title1")
                .put("status","open");
        
        String itemId2 = "5678";
        JsonObject json2 = new JsonObject()
                .put("projectId", itemId2)
                .put("ownerFirstName", "Jane")
                .put("ownerLastName", "Doe")
                .put("ownerEmailAddress", "jdoe@example.com")
                .put("projectTitle", "Title2")
                .put("status","closed");
        
        Async saveAsync = context.async(2);
        
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        
        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        
        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, mongoClient);

        Async async = context.async();

        service.getProjects( ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertEquals(2, ar.result().size());
                Set<String> itemIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
                assertThat(itemIds, allOf(hasItem(itemId),hasItem(itemId2)));
                async.complete();
            }
        });
    }
    
    @Test
    public void testGetProjectById(TestContext context) throws Exception {
        String itemId = "999999";
        JsonObject json1 = new JsonObject()
                .put("projectId", itemId)
                .put("ownerFirstName", "John")
                .put("ownerLastName", "Doe")
                .put("ownerEmailAddress", "jdoe@example.com")
                .put("projectTitle", "Title1")
                .put("status","open");
        
        Async saveAsync = context.async(1);
        
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        
        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, mongoClient);

        Async async = context.async();

        service.getProjectById(itemId, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
            	assertThat(ar.result(),notNullValue());
            	assertThat(ar.result().getOwnerFirstName(), equalTo("John"));
            	assertThat(ar.result().getProjectId(), equalTo(itemId));
            	async.complete();
            }
        });
    }
    
    @Test
    public void testGetNonExistingProjectById(TestContext context) throws Exception {
        String itemId = "999999";
        JsonObject json1 = new JsonObject()
                .put("projectId", itemId)
                .put("ownerFirstName", "John")
                .put("ownerLastName", "Doe")
                .put("ownerEmailAddress", "jdoe@example.com")
                .put("projectTitle", "Title1")
                .put("status","open");
        
        Async saveAsync = context.async(1);
        
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        
        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, mongoClient);

        Async async = context.async();

        service.getProjectById("34343", ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
            	assertThat(ar.result(),nullValue());
            	async.complete();
            }
        });
    }
 
}
