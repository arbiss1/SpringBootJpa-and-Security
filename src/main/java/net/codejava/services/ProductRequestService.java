package net.codejava.services;

import net.codejava.entities.ProductRequests;
import net.codejava.repository.ProductRequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductRequestService {

    @Autowired
    ProductRequestsRepository repoProducts;

    public java.util.List<ProductRequests> listAllProductRequested() {
        return repoProducts.findAll();
    }

    public void save(ProductRequests productRequests) {
        repoProducts.save(productRequests);
    }

    public boolean isRequestedProductPresent(ProductRequests productRequests) {
       return listAllProductRequested().stream().anyMatch(p -> p.getProduct().equals(productRequests.getProduct()));
    }


    public List<ProductRequests> listAllByProduct(Long userRequestedId) {
        return repoProducts.findByuserRequestedId(userRequestedId);
    }
    public void delete(long productRequests){repoProducts.deleteById(productRequests);}

    public ProductRequests get(String product) {
       return repoProducts.findByProduct(product).get();
    }
    }

