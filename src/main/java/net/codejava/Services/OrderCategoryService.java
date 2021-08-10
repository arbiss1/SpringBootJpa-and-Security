package net.codejava.Services;

import net.codejava.Repositories.OrderCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class OrderCategoryService {

    @Autowired
    private OrderCategoryRepository repoCategory;

    public List<net.codejava.Domains.OrderCategory> listAll() {
        return repoCategory.findAll();
    }

    public void save(net.codejava.Domains.OrderCategory category) {
        repoCategory.save(category);
    }

    public net.codejava.Domains.OrderCategory get(long categoryId) {
        return repoCategory.findById(categoryId).get();
    }

    public net.codejava.Domains.OrderCategory getCategory(String category) {
        return repoCategory.findByCategory(category).get();
    }

   public void delete(long categoryId){
        repoCategory.deleteById(categoryId);
   }

   public boolean isPresent(net.codejava.Domains.OrderCategory orderCategory){
        return listAll().stream().anyMatch(category -> category.getCategory().equals(orderCategory.getCategory()));
   }

   }


