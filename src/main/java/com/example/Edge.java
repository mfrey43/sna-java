package com.example;

import twitter4j.User;

import java.util.Objects;

public class Edge {
    private final User user1;
    private final User user2;
    private long retweetCount;

    public Edge(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.retweetCount = 1;
    }

    public String toLine() {
        return user1.getId() + "," + user2.getId() + "," + retweetCount + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(user1, edge.user1) && Objects.equals(user2, edge.user2) || Objects.equals(user2, edge.user1) && Objects.equals(user1, edge.user2);
    }

    @Override
    public int hashCode() {
        return user1.hashCode() + user2.hashCode();
    }
}
