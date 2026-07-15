package com.interviewdocs;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.Collections;
import java.util.Map;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.annotation.Secured;

@Controller("/")
public class HomeController {

    @Get
    public Map<String, Object> index() {
        return Collections.singletonMap("message", "Hello World");
    }
}
