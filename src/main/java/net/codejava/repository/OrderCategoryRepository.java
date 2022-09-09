package net.codejava.repository;

import net.codejava.entities.OrderCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderCategoryRepository extends JpaRepository <OrderCategory,Long> {

    Optional<OrderCategory> findByCategory (String category);



}
