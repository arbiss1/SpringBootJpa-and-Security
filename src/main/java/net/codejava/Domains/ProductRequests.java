package net.codejava.Domains;


import javax.persistence.*;

@Entity
public class ProductRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    private String product ;

    private long userRequestedId;

    public ProductRequests(){}

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public long getUserRequestedId() {
        return userRequestedId;
    }

    public void setUserRequestedId(long userRequestedId) {
        this.userRequestedId = userRequestedId;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public ProductRequests(long productId, String product) {
        this.productId = productId;
        this.product = product;
    }
}
