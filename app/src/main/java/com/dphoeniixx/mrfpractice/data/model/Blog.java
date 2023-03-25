package com.dphoeniixx.mrfpractice.data.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Blog {
    private final String title;
    private final String description;
    private final String image;
    private final String createdAt;
    private final String _id;
    private final ArrayList<String> tags;

    public Blog(String _id, String title, String description, String image, ArrayList<String> tags, String createdAt) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    public String getID() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Blog{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", tags=" + tags.toString() +
                '}';
    }
}
