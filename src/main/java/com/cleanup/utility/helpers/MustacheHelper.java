package com.cleanup.utility.helpers;

import com.cleanup.model.Authority;
import com.cleanup.model.dto.UserResponse;
import com.cleanup.utility.Constants;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MustacheHelper {

    private final MustacheFactory mustacheFactory;

    public MustacheHelper(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    public String compileSingleUser(String name, UserResponse response) {
        Mustache mustache = mustacheFactory.compile("%s/%s.mustache".formatted(Constants.VIEW_DIRECTORY, name));
        StringWriter writer = new StringWriter();
        response.getAuthorities().sort(Comparator.comparing(Authority::getId));
        mustache.execute(writer, response);
        return writer.toString();
    }

    public String compileMultipleUsers(String name, List<UserResponse> responses, String caller) {
        Mustache mustache = mustacheFactory.compile("%s/%s.mustache".formatted(Constants.VIEW_DIRECTORY, name));
        StringWriter writer = new StringWriter();
        responses.forEach(response -> response.getAuthorities().sort(Comparator.comparing(Authority::getId)));
        Map<String, Object> context = new HashMap<>();
        context.put("link", Constants.BASE_URL.concat("/admin/id"));
        context.put("filter", caller);
        context.put("responses", responses);
        mustache.execute(writer, context);
        return writer.toString();
    }
}
