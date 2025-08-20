package com.softmakers.manager_store.jpo;

public enum Gender {
    // 남성, 여성, 밝히고싶지않음
    M("M"), F("F"), P("P");

    private String label;

    private Gender(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
