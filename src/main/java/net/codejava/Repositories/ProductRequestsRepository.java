package net.codejava.Repositories;

import net.codejava.Domains.ProductRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRequestsRepository extends JpaRepository<ProductRequests,Long> {


    List<ProductRequests> findByuserRequestedId(Long userRequestedId);

    Optional<ProductRequests> findByProduct (String product);
}
