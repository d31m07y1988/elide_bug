package com.example.demo.controller;

import com.example.demo.CallElide;
import com.yahoo.elide.Elide;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
@ResponseBody
public class ElideController {
    private Elide engine;

    public ElideController(Elide engine) {
        this.engine = engine;
    }

    private String runElide(final HttpServletRequest request, final CallElide callElide) {
        final String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        final String fixedPath = restOfTheUrl.replaceAll("^/?api/", "");
        return callElide.call(engine, fixedPath);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value={"**"})
    public String get(@RequestParam final Map<String, String> allRequestParams, final HttpServletRequest request) {
        return runElide(request, ((elide1, path) -> engine.get(path, new MultivaluedHashMap<>(allRequestParams), new Object()).getBody()));
    }
}
