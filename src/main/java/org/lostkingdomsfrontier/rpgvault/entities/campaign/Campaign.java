package org.lostkingdomsfrontier.rpgvault.entities.campaign;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.mongojack.DBRef;
import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public class Campaign {
    @JsonView(JacksonViews.MongoView.class)
    private String _id;

    String name;

    String slug;  // slug also needs to be unique

    @JsonView(JacksonViews.MongoView.class)
    List<DBRef<Adventure,String>> adventures = new ArrayList<DBRef<Adventure, String>>();

    @JsonView(JacksonViews.RestfulView.class)
    IndexList adventureIndex = new IndexList();

    @ObjectId
    public String get_id() {
        return _id;
    }

    @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }

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

    public List<DBRef<Adventure, String>> getAdventures() {
        return adventures;
    }

    public void setAdventures(List<DBRef<Adventure, String>> adventures) {
        this.adventures = adventures;
    }

    public IndexList getAdventureIndex() {
        return adventureIndex;
    }

    public void setAdventureIndex(IndexList adventureIndex) {
        this.adventureIndex = adventureIndex;
    }
}
