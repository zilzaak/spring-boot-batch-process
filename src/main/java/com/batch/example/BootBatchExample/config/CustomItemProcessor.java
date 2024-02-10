package com.batch.example.BootBatchExample.config;

import com.batch.example.BootBatchExample.model.Product;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Product, Product> {
    @Override
    public Product process(Product item) throws Exception {
        // logic goes here

        int originalPrice=Integer.parseInt(item.getPrice());
        double discountPercantage=Double.parseDouble(item.getDiscount());
        double discount=(discountPercantage/100)*originalPrice;
        double finalPrice=originalPrice-discount;
        item.setDiscountedPrice(String.valueOf(finalPrice));
        return item;
    }
}
