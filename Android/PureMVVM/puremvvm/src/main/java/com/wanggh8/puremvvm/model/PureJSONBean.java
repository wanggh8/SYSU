package com.wanggh8.puremvvm.model;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2021/1/29
 */
public class PureJSONBean implements Serializable {
    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
