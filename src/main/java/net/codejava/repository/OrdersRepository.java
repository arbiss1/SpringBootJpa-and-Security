package net.codejava.repository;

import java.util.List;
import net.codejava.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId);

    List<Orders> findByUserIdAndOrderStatus(long userId , String orderStatus);

    Orders findByidAndOrderStatus(Long id , String orderStatus);

    List<Orders> findByOrderStatus(String orderStatus);

}
