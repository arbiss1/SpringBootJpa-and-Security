package net.codejava.Repositories;

import net.codejava.Domains.OrderCategory;
import net.codejava.Domains.OrderList;
import net.codejava.Services.OrderCategoryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderCategoryRepository extends JpaRepository <OrderCategory,Long> {

    Optional<OrderCategory> findByCategory (String category);



}
