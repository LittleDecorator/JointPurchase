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
import org.springframework.transaction.annotation.Transactional;

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
    @Query(value = "UPDATE Content c SET c.metaInfo=:meta WHERE c.id=:id")
    int updateContentMeta(@Param("meta") String meta, @Param("id") String id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Content SET content = :content WHERE id = :id", nativeQuery = true)
    void updateContentData(@Param("content") String data, @Param("id") String id);

    @Cacheable(value = "base64")
    @Query(value = "select content from Content c where c.id=?1", nativeQuery = true)
    String getContentData(String id);

}
