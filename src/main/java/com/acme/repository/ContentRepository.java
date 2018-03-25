package com.acme.repository;

import com.acme.model.Content;
import java.util.Collection;
import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import org.springframework.data.repository.query.Param;

/**
 * Created by nikolay on 19.02.17.
 *
 */
public interface ContentRepository extends CrudRepository<Content, String> {

    Content findOneByIsDefault(boolean isDefault);

    List<Content> findAllByIsInstagramTrue();

    List<Content> findAllByIdIn(Collection<String> ids);

    void deleteAllByIdIn(List<String> ids);

    @Modifying
    @Query(value = "update Content c set c.metaInfo=:meta where c.id=:id")
    int updateContentMeta(@Param("meta") String meta, @Param("id") String id);

    @Modifying
    @Query(value = "update content c set c.content=?1 where c.id=?2", nativeQuery = true)
    void updateContentData(String data, String id);

    @Cacheable(value = "base64")
    @Query(value = "select content from Content c where c.id=?1", nativeQuery = true)
    String getContentData(String id);

}
