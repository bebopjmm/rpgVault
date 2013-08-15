package org.lostkingdomsfrontier.rpgvault.entities;

/**
 * @author: bebopjmm
 * Date: 8/11/13 Time: 11:29
 */
public class JacksonViews {
    public static class DefaultView {}
    public static class MongoView extends DefaultView {}
    public static class RestfulView extends DefaultView {}

}
