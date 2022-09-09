package net.codejava.repository;

import net.codejava.entities.ProductRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRequestsRepository extends JpaRepository<ProductRequests,Long> {


    List<ProductRequests> findByuserRequestedId(Long userRequestedId);

    Optional<ProductRequests> findByProduct (String product);
}
