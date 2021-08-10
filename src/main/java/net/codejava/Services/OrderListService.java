package net.codejava.Services;

import java.util.List;
import java.util.Optional;

import net.codejava.Domains.OrderCategory;
import net.codejava.Domains.OrderList;
import net.codejava.Repositories.OrderListRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderListService {

    @Autowired
    public OrderListRepository repoList;
    @Autowired
    public OrderCategoryService repoCategory;

    public List<OrderList> listAllorders() {
        return repoList.findAll();
    }

    public OrderList get(String listName) {
        return repoList.findByListName(listName).get();
    }

    public void delete(long listId) {
        repoList.deleteById(listId);
    }

    public List<OrderList> deleteByCategory (OrderCategory categoryId){
        return repoList.findBylistId(categoryId);
    }

    public List<OrderList> listAllProductsByCategory(OrderCategory categoryId){
        return repoList.findBylistId(categoryId);
    }

//    public List<OrderList> listAllproductsBycategory(OrderCategory category){return repoList.findByCategory(Optional.ofNullable(category));}
}

