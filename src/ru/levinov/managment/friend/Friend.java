package ru.levinov.managment.friend;

/**
 * @author levin1337
 * @since 09.06.2023
 */
public class Friend {
    private String name;

    /**
     * ����������� Friend.
     *
     * @param name ��� �����
     */
    public Friend(String name) {
        this.name = name;
    }

    /**
     * ���������� ��� �����.
     *
     * @return ��� �����
     */
    public String getName() {
        return name;
    }

    /**
     * ������������� ��� �����.
     *
     * @param name ����� ��� �����
     */
    public void setName(String name) {
        this.name = name;
    }
}