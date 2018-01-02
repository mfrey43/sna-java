package com.example;

import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Cache {

    Logger logger = Logger.getLogger(TwitterScraper.class);

    private final Twitter twitter;

    private HashMap<String, User> userCache = new HashMap<>();
    private HashMap<Long, List<Status>> timelineCache = new HashMap<>();
    private HashMap<Long, List<Status>> retweetCache = new HashMap<>();
    private HashMap<Long, List<User>> followerListCache = new HashMap<>();

    public Cache() {
        Properties prop = new Properties();
        try(InputStream input = new FileInputStream("twitter4j.properties")) {
            prop.load(input);
        } catch (IOException e) {
            System.err.println("don't forget to create your own twitter4j.properties file");
            e.printStackTrace();
        }
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(prop.getProperty("oAuthConsumerKey"))
                .setOAuthConsumerSecret(prop.getProperty("oAuthConsumerSecret"))
                .setOAuthAccessToken(prop.getProperty("oAuthAccessToken"))
                .setOAuthAccessTokenSecret(prop.getProperty("oAuthAccessTokenSecret"));

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public User showUser(String screenName) throws TwitterException {
        if (userCache.containsKey(screenName)) {
            logger.debug("showUser cache hit");
            return userCache.get(screenName);
        } else {
            User user = twitter.showUser(screenName);
            userCache.put(screenName, user);
            return user;
        }
    }

    public List<Status> getUserTimeline(long userId) throws TwitterException {
        if (timelineCache.containsKey(userId)) {
            logger.debug("getUserTimeline cache hit");
            return timelineCache.get(userId);
        } else {
            List<Status> list = new ArrayList<>(twitter.getUserTimeline(userId));
            timelineCache.put(userId, list);
            return list;
        }
    }

    public List<Status> getRetweets(long statusId) throws TwitterException {
        if (retweetCache.containsKey(statusId)) {
            logger.debug("getRetweets cache hit");
            return retweetCache.get(statusId);
        } else {
            List<Status> list = new ArrayList<>(twitter.getRetweets(statusId));
            retweetCache.put(statusId, list);
            return list;
        }
    }

    public List<User> getFollowersList(long userId) throws TwitterException {
        if (followerListCache.containsKey(userId)) {
            logger.debug("getFollowersList cache hit");
            return followerListCache.get(userId);
        } else {
            List<User> list = new ArrayList<>(twitter.getFollowersList(userId, -1L));
            followerListCache.put(userId, list);
            return list;
        }
    }

    public void save(){
        try(FileOutputStream fout = new FileOutputStream("userCache.ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(userCache);
            logger.debug("userCache saved to disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fout = new FileOutputStream("timelineCache.ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(timelineCache);
            logger.debug("timelineCache saved to disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fout = new FileOutputStream("retweetCache.ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(retweetCache);
            logger.debug("retweetCache saved to disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fout = new FileOutputStream("followerListCache.ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(followerListCache);
            logger.debug("followerListCache saved to disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try(FileInputStream fin = new FileInputStream("userCache.ser")) {
            ObjectInputStream oos = new ObjectInputStream(fin);
            userCache = (HashMap) oos.readObject();
            logger.debug("userCache file loaded");
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(FileInputStream fin = new FileInputStream("timelineCache.ser")) {
            ObjectInputStream oos = new ObjectInputStream(fin);
            timelineCache = (HashMap) oos.readObject();
            logger.debug("timelineCache file loaded");
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(FileInputStream fin = new FileInputStream("retweetCache.ser")) {
            ObjectInputStream oos = new ObjectInputStream(fin);
            retweetCache = (HashMap) oos.readObject();
            logger.debug("retweetCache file loaded");
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(FileInputStream fin = new FileInputStream("followerListCache.ser")) {
            ObjectInputStream oos = new ObjectInputStream(fin);
            followerListCache = (HashMap) oos.readObject();
            logger.debug("followerListCache file loaded");
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printLimits() {
        try {
            twitter.getRateLimitStatus().forEach((key, val) -> {
                System.out.println(key + ": " + val.getRemaining() + "/" + val.getLimit());
            });
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
