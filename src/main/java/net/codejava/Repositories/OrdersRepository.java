package net.codejava.Repositories;

import java.util.List;
import java.util.Optional;

import net.codejava.Domains.Orders;
import net.codejava.Services.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId);


    Optional<Orders> findBylistName(String listName);
}
