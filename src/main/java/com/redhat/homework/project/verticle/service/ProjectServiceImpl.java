package com.redhat.homework.project.verticle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.redhat.homework.project.model.Project;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ProjectServiceImpl implements ProjectService {

    private MongoClient client;

    public ProjectServiceImpl(Vertx vertx, MongoClient client) {
        this.client = client;
    }

	@Override
	public void getProjects(Handler<AsyncResult<List<Project>>> resulthandler) {
	       JsonObject query = new JsonObject();
	        client.find("projects", query, ar -> {
	            if (ar.succeeded()) {
	                List<Project> products = ar.result().stream()
	                                           .map(json -> new Project(json))
	                                           .collect(Collectors.toList());
	                resulthandler.handle(Future.succeededFuture(products));
	            } else {
	                resulthandler.handle(Future.failedFuture(ar.cause()));
	            }
	        });
		
	}

	@Override
	public void getProjectById(String itemId, Handler<AsyncResult<Project>> resulthandler) {
	    JsonObject query = new JsonObject().put("projectId", itemId);
        client.find("projects", query, ar -> {
            if (ar.succeeded()) {
                Optional<JsonObject> result = ar.result().stream().findFirst();
                if (result.isPresent()) {
                    resulthandler.handle(Future.succeededFuture(new Project(result.get())));
                } else {
                    resulthandler.handle(Future.succeededFuture(null));
                }
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
	}

	@Override
	public void getProjectsByStatus(String status, Handler<AsyncResult<List<Project>>> resulthandler) {
	       JsonObject query = new JsonObject().put("status", status);
	        client.find("projects", query, ar -> {
	            if (ar.succeeded()) {
	                List<Project> products = ar.result().stream()
	                                           .map(json -> new Project(json))
	                                           .collect(Collectors.toList());
	                resulthandler.handle(Future.succeededFuture(products));
	            } else {
	                resulthandler.handle(Future.failedFuture(ar.cause()));
	            }
	        });
	}

	@Override
	public void addProject(Project product, Handler<AsyncResult<String>> resulthandler) {
		client.save("projects", product.toJson(), resulthandler);
	}


}
