package net.codejava.Repositories;

import net.codejava.Domains.OrderCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderCategoryRepository extends JpaRepository <OrderCategory,Long> {

    Optional<OrderCategory> findByCategory (String category);

}
