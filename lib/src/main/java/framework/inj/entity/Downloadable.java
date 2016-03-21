package framework.inj.entity;

/**
 * a downloadable entity defines how a entity should be downloaded
 * notice that this entity that Downloadable directs to download could be either Downloadable itself or some one else
 * Downloadable only admits to provider the necessary info and status, not the entity itself
 * Downloadable does NOT have to define widgets logic
 * When this entity that Downloadable directs to download is Downloadable itself
 * all states will be lost after it finishes loading
 */
public interface Downloadable extends Director, Responsible {

}
