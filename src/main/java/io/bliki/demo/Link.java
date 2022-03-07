package io.bliki.demo;

import com.fasterxml.jackson.databind.introspect.*;

public record Link(String href, String text, int rating, String description, String categoryId, String tags) {

    public String[] tagsArray() {
        if (tags() != null) {
            return tags().split(",");
        }
        return new String[0];
    }
}
