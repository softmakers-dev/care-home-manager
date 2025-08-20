package com.softmakers.manager_store.vo;

public enum ImageType {
    PNG("PNG"), JPG("JPG"), JPEG("JPEG");

    private String label;

    private ImageType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
