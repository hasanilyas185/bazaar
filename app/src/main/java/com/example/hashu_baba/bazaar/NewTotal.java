package com.example.hashu_baba.bazaar;

public class NewTotal {
   Integer Total;
   Integer GrandTotal=0;

    public Integer getTotal() {
        return Total;
    }

    public void setTotal(Integer total) {
        Total = total;
    }

    public Integer getGrandTotal() {
        return GrandTotal;
    }

    public void setGrandTotal(Integer total) {
        GrandTotal = GrandTotal +total;
    }

    public NewTotal(Integer total, Integer grandTotal) {

        Total = total;
        GrandTotal = grandTotal;
    }
}
