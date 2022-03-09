package io.bliki.demo;

public record Link(String href, String text, int rating, String description, String categoryId, String tags, Language language) {

    public String[] tagsArray() {
        if (tags() != null) {
            return tags().split(",");
        }
        return new String[0];
    }
}
