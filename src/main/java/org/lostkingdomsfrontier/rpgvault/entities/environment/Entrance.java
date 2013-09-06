package org.lostkingdomsfrontier.rpgvault.entities.environment;

import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public class Entrance {

    String name;

    String slug;

    String description;

    String areas[] = new String[2];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAreas() {
        return areas;
    }

    public void connectAreas(Area area1, Area area2) {
        this.areas[0] = area1.getSlug();
        this.areas[1] = area2.getSlug();
    }
}
