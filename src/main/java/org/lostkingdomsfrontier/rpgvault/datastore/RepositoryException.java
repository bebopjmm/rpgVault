package org.lostkingdomsfrontier.rpgvault.datastore;

import com.mongodb.MongoException;

/**
 * @author: bebopjmm
 * Date: 8/13/13 Time: 10:09
 */
public class RepositoryException extends MongoException {

    public RepositoryException(String msg) {
        super(msg);
    }

    public RepositoryException(int code, String msg) {
        super(code, msg);
    }
}
