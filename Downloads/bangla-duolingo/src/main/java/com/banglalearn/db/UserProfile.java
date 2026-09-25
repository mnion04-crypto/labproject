package com.banglalearn.db;

public record UserProfile(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
