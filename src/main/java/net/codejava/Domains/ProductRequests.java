package net.codejava.Domains;


import javax.persistence.*;

@Entity
public class ProductRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    private String product ;

    private long userRequestedId;

    private String requestedBy;

    private String status;

    public ProductRequests(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

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

}
