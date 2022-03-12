package io.bliki.demo;

import java.math.*;

public record Link(String id, String href, String text, int rating, BigDecimal communityRating, String description, String categoryId, String tags, Language language) {

    public String[] tagsArray() {
        if (tags() != null) {
            return tags().split(",");
        }
        return new String[0];
    }

    public String communityRatingAsString() {
        return communityRating == null ? "?": communityRating.setScale(1).toString();
    }
}
