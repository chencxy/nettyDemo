package com.cxy.demo.netty_demo.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

@Path("/protected")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class DemoService {

	@Path("/about")
    @GET
    public String about(){
		return "test";
	}
}
