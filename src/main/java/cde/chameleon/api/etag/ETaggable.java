package cde.chameleon.api.etag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * The interface ETaggable adds the calculation of an ETag to the implementing class.
 * @see <a href="http://opensource.zalando.com/restful-api-guidelines/#optimistic-locking">
 *     http://opensource.zalando.com/restful-api-guidelines/#optimistic-locking</a>
 */
public interface ETaggable {

    String getETag();
    void setETag(String eTag);

    default void updateETag() {
        setETag(null);
        try {
            setETag(DigestUtils.md5Hex(new ObjectMapper().writeValueAsString(this)));
        } catch (JsonProcessingException _) {
            throw new IllegalStateException("Unable to calculate ETag!");
        }
    }
}
