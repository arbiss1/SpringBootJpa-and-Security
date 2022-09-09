package net.codejava.repository;

import java.util.List;
import java.util.Optional;

import net.codejava.entities.OrderCategory;
import net.codejava.entities.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderListRepository extends JpaRepository<OrderList, Long> {

	Optional <OrderList> findByListName (String listName);

    List<OrderList> findByCategory(Optional<OrderCategory> category);

    List<OrderList> findBycategory(OrderCategory category);

//
//    @Query("select new OrderList(listId , listName) from OrderList where category_category_id = :id")
//    public List<OrderList> findByOrderCategory(@Param("id") long id);

}




