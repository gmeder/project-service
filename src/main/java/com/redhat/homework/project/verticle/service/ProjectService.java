package com.redhat.homework.project.verticle.service;

import java.util.List;

import com.redhat.homework.project.model.Project;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

@ProxyGen
public interface ProjectService {

    final static String ADDRESS = "project-service";

    static ProjectService create(Vertx vertx, JsonObject config, MongoClient client) {
        return new ProjectServiceImpl(vertx, client);
    }

    static ProjectService createProxy(Vertx vertx) {
        return new ProjectServiceVertxEBProxy(vertx, ADDRESS);
    }

    void getProjects(Handler<AsyncResult<List<Project>>> resulthandler);

    void getProjectById(String itemId, Handler<AsyncResult<Project>> resulthandler);

    void getProjectsByStatus(String status, Handler<AsyncResult<List<Project>>> resulthandler);

    void addProject(Project product, Handler<AsyncResult<String>> resulthandler);

}
