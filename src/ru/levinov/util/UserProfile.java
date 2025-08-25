package ru.levinov.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author levin1337
 * @since 25.06.2023
 */
@Getter
@Setter
@AllArgsConstructor
public class UserProfile {
    private final String name;
    private final String role;
}
