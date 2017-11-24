package com.acme.repository;

import com.acme.model.InstagramPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by nikolay on 03.07.17.
 */
public interface InstagramPostRepository extends JpaRepository<InstagramPost, String>, JpaSpecificationExecutor<InstagramPost> {

    List<InstagramPost> findAllByWrongPostFalse();

    List<InstagramPost> findAllByShowOnMainIsTrue();

}
