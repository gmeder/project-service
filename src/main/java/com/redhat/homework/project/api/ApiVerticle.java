package com.redhat.homework.project.api;

import java.util.List;

import com.redhat.homework.project.model.Project;
import com.redhat.homework.project.verticle.service.ProjectService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ApiVerticle extends AbstractVerticle {

	private ProjectService projectService;

	public ApiVerticle(ProjectService catalogService) {
		this.projectService = catalogService;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		Router router = Router.router(vertx);
		router.get("/projects").handler(this::getProjects);
		router.get("/projects/:projectId").handler(this::getProjectById);
		router.get("/projects/status/:status").handler(this::getProjectsByStatus);
		router.route("/projects").handler(BodyHandler.create());
		router.post("/projects").handler(this::addProject);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
					}
				});
	}

	private void getProjects(RoutingContext rc) {
		projectService.getProjects(ar -> {
			if (ar.succeeded()) {
				List<Project> products = ar.result();
				JsonArray json = new JsonArray();
				products.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectById(RoutingContext rc) {
		String itemId = rc.request().getParam("projectId");
		projectService.getProjectById(itemId, ar -> {
			if (ar.succeeded()) {
				Project product = ar.result();
				JsonObject json;
				if (product != null) {
					json = product.toJson();
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectsByStatus(RoutingContext rc) {

		String status = rc.request().getParam("status");

		projectService.getProjectsByStatus(status, ar -> {
			if (ar.succeeded()) {
				List<Project> products = ar.result();
				if (products != null && !products.isEmpty()) {
					JsonArray json = new JsonArray();
					products.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});

	}

	private void addProject(RoutingContext rc) {
		JsonObject json = rc.getBodyAsJson();
		projectService.addProject(new Project(json), ar -> {
			if (ar.succeeded()) {
				rc.response().setStatusCode(201).end();
			} else {
				rc.fail(ar.cause());
			}
		});
	}

}
