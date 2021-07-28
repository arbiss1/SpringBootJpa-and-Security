package net.codejava.Repositories;

import java.util.Optional;

import net.codejava.Domains.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderListRepository extends JpaRepository<OrderList, Long> {

	Optional <OrderList> findByListName (String listName);
	
}


