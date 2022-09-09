package net.codejava.services;

import net.codejava.repository.OrderCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class OrderCategoryService {

    @Autowired
    private OrderCategoryRepository repoCategory;

    public List<net.codejava.entities.OrderCategory> listAll() {
        return repoCategory.findAll();
    }

    public void save(net.codejava.entities.OrderCategory category) {
        repoCategory.save(category);
    }

    public net.codejava.entities.OrderCategory get(long categoryId) {
        return repoCategory.findById(categoryId).get();
    }

    public net.codejava.entities.OrderCategory getCategory(String category) {
        return repoCategory.findByCategory(category).get();
    }

   public void delete(long categoryId){
        repoCategory.deleteById(categoryId);
   }

   public boolean isPresent(net.codejava.entities.OrderCategory orderCategory){
        return listAll().stream().anyMatch(category -> category.getCategory().equals(orderCategory.getCategory()));
   }
}


