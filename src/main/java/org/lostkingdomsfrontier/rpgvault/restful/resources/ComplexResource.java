package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.datastore.SettingRepositoryDelegate;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Area;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Complex;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.logging.Logger;

/**
 * @author bebopjmm Date: 8/30/13 Time: 15:17
 */
public class ComplexResource {
    private static final Logger LOG = Logger.getLogger(ComplexResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;
    private Complex complex;
    private SettingRepositoryDelegate delegate;

    public void setDelegate(SettingRepositoryDelegate delegate) {
        if (delegate == null) {
            LOG.warning("Setting NULL delegate!");
        }
        this.delegate = delegate;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public Complex getComplex() {
        // No need to map areas because they are embedded not referenced.
        return this.complex;
    }

    public void setComplex(Complex complex) {
        if (complex == null) {
            LOG.warning("Setting NULL complex!");
        }
        this.complex = complex;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Complex updateComplex(Complex complex) {
        this.complex = this.delegate.replaceComplex(complex).getSavedObject();
        return this.complex;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String addArea(Area area) {
        this.delegate.addAreaToComplex(area, this.complex.getSlug());
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI areaURI = ub.path(area.getSlug()).build();
        return areaURI.toASCIIString();
    }

    @Path("/area/{id}")
    public Area getArea(@PathParam("id") String slug) {
        for (Area complexArea : complex.getAreas()) {
            if (complexArea.getSlug().equalsIgnoreCase(slug)) {
                return complexArea;
            }
        }
        throw new WebApplicationException(404);
    }
}
