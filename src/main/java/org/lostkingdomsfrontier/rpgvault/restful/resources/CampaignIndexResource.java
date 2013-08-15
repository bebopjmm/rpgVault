package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Campaign;
import org.lostkingdomsfrontier.rpgvault.restful.RepositoryDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: bebopjmm
 * Date: 8/10/13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
@Path("/campaigns")
public class CampaignIndexResource {
    private static final Logger LOG = Logger.getLogger(CampaignIndexResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public IndexList getCampaignsIndex() {
        LOG.info("-- Looking up campaigns...");
        Response.ResponseBuilder builder = null;
        IndexList results = new IndexList();
        List<Campaign> campaigns = RepositoryDao.VAULT_REPOSITORY.retrieveCampaigns();
        LOG.info("received # campaigns = " + campaigns.size());
        if (campaigns.isEmpty()) {
            LOG.info("-- No campaigns in repository");
            builder = Response.noContent();
        } else {
            LOG.info("-- Assembling CampaignIndex Response, total resource indices = " + campaigns.size());
            for (Campaign campaign : campaigns) {
                LOG.info("-- results should contain campaign: " + campaign.getName());
                ResourceIndex index = new ResourceIndex();
                index.setId(campaign.getSlug());
                UriBuilder ub = uriInfo.getAbsolutePathBuilder();
                URI psURI = ub.path(campaign.getSlug()).build();
                index.setUri(psURI.toASCIIString());
                index.setName(campaign.getName());
                results.getIndices().add(index);
            }
            builder = Response.ok(results);
            builder.type(MediaType.APPLICATION_JSON_TYPE);
        }

//        return builder.build();
        return results;
    }


    @Path("/{id}")
    public CampaignResource getCampaign(@PathParam("id") String slug) {
        CampaignResource  resource = resourceContext.getResource(CampaignResource.class);
        // Load the specific campaign
        resource.setCampaign(RepositoryDao.VAULT_REPOSITORY.getRepository().getCampaign(slug));
        return resource;
    }
}
