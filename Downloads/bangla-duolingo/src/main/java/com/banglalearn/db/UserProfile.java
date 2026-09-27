package com.banglalearn.db;

public record UserProfile(int id, String name, String pin) {
    @Override
    public String toString() {
        return name;
    }
}
