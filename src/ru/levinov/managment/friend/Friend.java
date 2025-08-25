package ru.levinov.managment.friend;

/**
 * @author levin1337
 * @since 09.06.2023
 */
public class Friend {
    private String name;

    /**
     * Конструктор Friend.
     *
     * @param name имя друга
     */
    public Friend(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя друга.
     *
     * @return имя друга
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя друга.
     *
     * @param name новое имя друга
     */
    public void setName(String name) {
        this.name = name;
    }
}