package com.example.design.factory.after.af.mac;

import lombok.Data;

/**
 * @author SuccessZhang
 * @date 2020/05/08
 */
@Data
public class MacBookAir implements MacBook {
    @Override
    public void open() {
        System.out.println("MacBookAir开机");
    }
}
