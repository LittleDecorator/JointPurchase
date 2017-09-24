package com.acme.repository;

import com.acme.model.Wishlist;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by nikolay on 24.09.17.
 */
public interface WishlistRepository extends JpaRepository<Wishlist, String>, JpaSpecificationExecutor<Wishlist> {

    @Transactional(noRollbackFor = {Exception.class, EmptyResultDataAccessException.class})
    void deleteAllByEmail(String email);

    List<Wishlist> findAllByEmail(String email);

    List<Wishlist> findByEmail(String email);

    int countByEmail(String email);

}
