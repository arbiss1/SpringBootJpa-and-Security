package net.codejava.Services;

import java.util.List;
import java.util.Optional;

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

    public Optional<OrderList> listAllByProductCategory(Optional<OrderCategory> category) {
        return repoList.findByCategory(category);
    }
}

