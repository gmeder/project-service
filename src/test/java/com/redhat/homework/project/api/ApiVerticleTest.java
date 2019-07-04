package com.redhat.homework.project.api;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.redhat.homework.project.model.Project;
import com.redhat.homework.project.verticle.service.ProjectService;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

    private Vertx vertx;
    private Integer port;
    private ProjectService projectService;

    @Before
    public void setUp(TestContext context) throws IOException {
      vertx = Vertx.vertx();

      vertx.exceptionHandler(context.exceptionHandler());

      ServerSocket socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();

      DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
      
      projectService = mock(ProjectService.class);
      vertx.deployVerticle(new ApiVerticle(projectService), options, context.asyncAssertSuccess());
    }

    /**
     * This method, called after our test, just cleanup everything by closing
     * the vert.x instance
     *
     * @param context
     *            the test context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
    
    @Test
    public void testGetProjects(TestContext context) {
    	  String itemId1 = "111111";
          JsonObject json1 = new JsonObject()
                  .put("projectId", itemId1)
                  .put("ownerFirstName", "John")
                  .put("ownerLastName", "Doe")
                  .put("ownerEmailAddress", "jdoe@example.com")
                  .put("projectTitle", "Title1")
                  .put("status","open");
          String itemId2 = "222222";
          JsonObject json2 = new JsonObject()
                  .put("projectId", itemId2)
                  .put("ownerFirstName", "John")
                  .put("ownerLastName", "Doe")
                  .put("ownerEmailAddress", "jdoe@example.com")
                  .put("projectTitle", "Title1")
                  .put("status","open");
          
          List<Project> products = new ArrayList<>();
          products.add(new Project(json1));
          products.add(new Project(json2));
          
          doAnswer(new Answer<Void>() {
              public Void answer(InvocationOnMock invocation){
                  Handler<AsyncResult<List<Project>>> handler = invocation.getArgument(0);
                  handler.handle(Future.succeededFuture(products));
                  return null;
               }
           }).when(projectService).getProjects(any());

          Async async = context.async();
          vertx.createHttpClient().get(port, "localhost", "/projects", response -> {
                  assertThat(response.statusCode(), equalTo(200));
                  assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                  response.bodyHandler(body -> {
                      JsonArray json = body.toJsonArray();
                      Set<String> itemIds =  new HashSet<>();
                      itemIds.add(json.getJsonObject(0).getString("projectId"));
                      itemIds.add(json.getJsonObject(1).getString("projectId"));
                      assertThat(itemIds.size(), equalTo(2));
                      assertThat(itemIds, allOf(hasItem(itemId1),hasItem(itemId2)));
                      verify(projectService).getProjects(any());
                      async.complete();
                  })
                  .exceptionHandler(context.exceptionHandler());
              })
          .exceptionHandler(context.exceptionHandler())
              .end();
    }
    
    @Test
    public void testGetProjectById(TestContext context) {
    	  String itemId1 = "111111";
          JsonObject json1 = new JsonObject()
                  .put("projectId", itemId1)
                  .put("ownerFirstName", "John")
                  .put("ownerLastName", "Doe")
                  .put("ownerEmailAddress", "jdoe@example.com")
                  .put("projectTitle", "Title1")
                  .put("status","open");
          
          
          doAnswer(new Answer<Void>() {
              public Void answer(InvocationOnMock invocation){
                  Handler<AsyncResult<Project>> handler = invocation.getArgument(1);
                  handler.handle(Future.succeededFuture(new Project(json1)));
                  return null;
               }
           }).when(projectService).getProjectById(any(),any());

          Async async = context.async();
          vertx.createHttpClient().get(port, "localhost", "/projects/111111", response -> {
                  assertThat(response.statusCode(), equalTo(200));
                  assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                  response.bodyHandler(body -> {
                      JsonObject json = body.toJsonObject();
                      assertThat(json.getString("projectId"),equalTo("111111"));
                      assertThat(json.getString("ownerFirstName"),equalTo("John"));
                      verify(projectService).getProjectById(any(),any());
                      async.complete();
                  })
                  .exceptionHandler(context.exceptionHandler());
              })
          .exceptionHandler(context.exceptionHandler())
              .end();
    }
    
    @Test
    public void testGetProjectByStatus(TestContext context) {
    	  String itemId1 = "111111";
          JsonObject json1 = new JsonObject()
                  .put("projectId", itemId1)
                  .put("ownerFirstName", "John")
                  .put("ownerLastName", "Doe")
                  .put("ownerEmailAddress", "jdoe@example.com")
                  .put("projectTitle", "Title1")
                  .put("status","open");
          String itemId2 = "222222";
          JsonObject json2 = new JsonObject()
                  .put("projectId", itemId2)
                  .put("ownerFirstName", "John")
                  .put("ownerLastName", "Doe")
                  .put("ownerEmailAddress", "jdoe@example.com")
                  .put("projectTitle", "Title1")
                  .put("status","open");
          
          List<Project> products = new ArrayList<>();
          products.add(new Project(json1));
          products.add(new Project(json2));
          
          doAnswer(new Answer<Void>() {
              public Void answer(InvocationOnMock invocation){
                  Handler<AsyncResult<List<Project>>> handler = invocation.getArgument(1);
                  handler.handle(Future.succeededFuture(products));
                  return null;
               }
           }).when(projectService).getProjectsByStatus(any(), any());

          Async async = context.async();
          vertx.createHttpClient().get(port, "localhost", "/projects/status/open", response -> {
                  assertThat(response.statusCode(), equalTo(200));
                  assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                  response.bodyHandler(body -> {
                      JsonArray json = body.toJsonArray();
                      Set<String> itemIds =  new HashSet<>();
                      itemIds.add(json.getJsonObject(0).getString("projectId"));
                      itemIds.add(json.getJsonObject(1).getString("projectId"));
                      assertThat(itemIds, allOf(hasItem(itemId1),hasItem(itemId2)));
                      verify(projectService).getProjectsByStatus(any(), any());
                      async.complete();
                  })
                  .exceptionHandler(context.exceptionHandler());
              })
          .exceptionHandler(context.exceptionHandler())
              .end();
    }

}
