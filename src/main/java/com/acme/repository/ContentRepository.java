package com.acme.repository;

import com.acme.model.Content;
import java.util.Collection;
import java.util.Set;
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

    @Query(value = "update Content c set c.metaInfo=:meta where c.id=:id")
    @Modifying
    int updateContentMeta(@Param("meta") String meta, @Param("id") String id);

}
