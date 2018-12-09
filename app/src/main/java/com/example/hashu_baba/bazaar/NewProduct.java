package com.example.hashu_baba.bazaar;

public class NewProduct {

    String ProductName;
    Integer ProductPrice;
    Integer Quantity;
    Integer Total;


    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Integer getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(Integer productPrice) {
        ProductPrice = productPrice;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }

    public Integer getTotal() {
        return Total;
    }

    public void setTotal(Integer quantity ,Integer productPrice) {
        Total = quantity*productPrice;
    }


    public NewProduct(String productName, Integer productPrice, Integer quantity, Integer total) {

        ProductName = productName;
        ProductPrice = productPrice;
        Quantity = quantity;
        Total = total;

    }
}
