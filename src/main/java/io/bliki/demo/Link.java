package io.bliki.demo;

public record Link(String id, String href, int rating, String description, String categoryId, String tags, Language language) {

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
