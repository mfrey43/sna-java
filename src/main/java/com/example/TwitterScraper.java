package com.example;

import org.apache.log4j.Logger;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TwitterScraper {

    Logger logger = Logger.getLogger(TwitterScraper.class);

    private ArrayList<String> politicians = new ArrayList<>();
    private Set<Node> nodes = new HashSet<>();
    private Set<Edge> edges = new HashSet<>();
    private final Cache cache;

    public static void main(String[] args) {
        TwitterScraper twitterScraper = new TwitterScraper();
        twitterScraper.run();
    }

    public TwitterScraper(){
        cache = new Cache();
    }

    public void run() {
        cache.load();

        try (BufferedReader br = new BufferedReader(new FileReader("politicians.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                politicians.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (String politician : politicians) {
                String[] data = politician.split(",");
                logger.info("loading data for: " + politician);
                retrievePolitician(data[0], data[1]);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        writeCSV();
        cache.save();
    }

    private void retrievePolitician(String name, String party) throws TwitterException {
        User politician = cache.showUser(name);

        addPolitician(politician, party);
        cache.getFollowersList(politician.getId()).forEach(follower -> addFollowerToPolitician(follower, politician));

        for (Status status : cache.getUserTimeline(politician.getId())) {
            if (status.getRetweetCount() > 0) {
                for (Status retweet : cache.getRetweets(status.getId())) {
                    User retweetUser = retweet.getUser();

                    // can retweet while not being a follower, this is a workaround
                    addFollowerToPolitician(retweetUser, politician);

                    for (User ff : cache.getFollowersList(retweetUser.getId())) {
                        addFollowerToFollower(ff, retweetUser);
                    }
                }
            }
        }
    }

    private void addPolitician(User politician, String party) {
        nodes.add(new Node(politician, Node.Type.POLITICIAN, party));
    }

    private void addFollowerToPolitician(User follower, User politician) {
        nodes.add(new Node(follower, Node.Type.FOLLOWER));
        edges.add(new Edge(politician, follower));
    }

    private void addFollowerToFollower(User followerFollower, User follower) {
        nodes.add(new Node(followerFollower, Node.Type.FOLLOWERFOLLOWER));
        edges.add(new Edge(follower, followerFollower));
    }

    private void writeCSV() {
        try (FileWriter writer = new FileWriter("nodes.csv")) {
            writer.write("id,twittername,realname,party,type\n");
            for (Node node : nodes) {
                writer.write(node.toLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter("edges.csv")) {
            writer.write("source,target,weight\n");
            for (Edge edge : edges) {
                writer.write(edge.toLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
