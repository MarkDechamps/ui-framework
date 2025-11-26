package org.example.dto;

/**
 * Eenvoudige DTO voor postcode-lookup resultaten.
 */
public class PostcodeDto {
    private final String id;
    private final String code;
    private final String name;

    public PostcodeDto(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
