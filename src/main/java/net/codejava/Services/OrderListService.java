package net.codejava.Services;

import java.util.List;

import net.codejava.Domains.OrderCategory;
import net.codejava.Domains.OrderList;
import net.codejava.Repositories.OrderListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderListService {

    @Autowired
    public OrderListRepository repoList;

    public List<OrderList> listAllorders() {
        return repoList.findAll();
    }

    public OrderList get(String listName) {
        return repoList.findByListName(listName).get();
    }

    public void delete(long listId) {
        repoList.deleteById(listId);
    }

    public List<OrderList> getbyCategory(OrderCategory category){return repoList.findBycategory(category);}

//
//    public List<OrderList> findByOrderCategory(int id) {
//        return repoList.findByOrderCategory(id);
//    }

}

