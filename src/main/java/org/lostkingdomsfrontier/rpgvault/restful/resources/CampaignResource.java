package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Adventure;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Campaign;
import org.lostkingdomsfrontier.rpgvault.restful.RepositoryDao;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: bebopjmm
 * Date: 8/11/13 Time: 11:57
 */
public class CampaignResource {
    private static final Logger LOG = Logger.getLogger(CampaignResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;
    private Campaign campaign;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public Campaign getCampaign() {
        this.campaign.setAdventureIndex(mapAdventures());
        return this.campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    IndexList mapAdventures() {
        IndexList results = new IndexList();
        List<Adventure> adventures = RepositoryDao.VAULT_REPOSITORY.retrieveAdventuresForCampaign(this.campaign);
        LOG.info("-- total adventures for this campaign = " + adventures.size());
        for (Adventure adventure : adventures) {
            LOG.info("-- results should contain adventure: " + adventure.getName());
            ResourceIndex index = new ResourceIndex();
            index.setId(adventure.getSlug());
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI psURI = ub.path(adventure.getSlug()).build();
            index.setUri(psURI.toASCIIString());
            index.setName(adventure.getName());
            results.getIndices().add(index);
        }
        return results;
    }
}
