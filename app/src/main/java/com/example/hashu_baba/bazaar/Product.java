package com.example.hashu_baba.bazaar;

public class Product {

    String ProductName;
    String ProductPrice;
    String Quantity;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public Product(String productName, String productPrice, String quantity) {

        ProductName = productName;
        ProductPrice = productPrice;
        Quantity = quantity;
    }
}
