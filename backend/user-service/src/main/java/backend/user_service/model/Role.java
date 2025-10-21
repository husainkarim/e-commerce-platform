package backend.user_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    CLIENT,
    SELLER;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "client" -> CLIENT;
            case "seller" -> SELLER;
            default -> throw new IllegalArgumentException("Invalid user type: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case CLIENT -> "client";
            case SELLER -> "seller";
        };
    }
}
