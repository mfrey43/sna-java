package com.example;

import twitter4j.User;

import java.util.Objects;

public class Node {
    public enum Type {
        POLITICIAN, FOLLOWER, FOLLOWERFOLLOWER
    }

    private final User user;
    private final Type type;
    private final String party;

    public Node(User user, Type type) {
        this(user, type, "N/A");
    }

    public Node(User user, Type type, String party) {
        this.user = user;
        this.type = type;
        this.party = party;
    }

    public String toLine() {
        return user.getId() + "," + user.getScreenName() + "," + user.getName() + "," + party + "," + type.name() + "\n";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(user, node.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
