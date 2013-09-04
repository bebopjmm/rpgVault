package org.lostkingdomsfrontier.rpgvault.entities.environment;

import java.util.ArrayList;
import java.util.List;

/**
 * An Area is a bounded space with a designated game purpose. It may be physically enclosed, like a room, or open, like
 * a wooded glade. Areas are connected by entrances, which may have barriers (e.g, a door).
 *
 * @author John McCormick Date: 8/10/13
 */
public class Area {

    /**
     * Descriptive title for the area
     */
    String name;
    /**
     * Readable identifier used to reference this area
     */
    String slug;
    /**
     * Area description typically shared with the players
     */
    String description;
    /**
     * Area detail notes typically reserved for GM use
     */
    String details;
    /**
     * The list of entrances that connect this area to others.
     */
    List<Entrance> entrances = new ArrayList<>();

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Entrance> getEntrances() {
        return entrances;
    }

    public void setEntrances(List<Entrance> entrances) {
        this.entrances = entrances;
    }
}
