package net.codejava.Repositories;

import java.util.List;

import net.codejava.Domains.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId);



	
}
