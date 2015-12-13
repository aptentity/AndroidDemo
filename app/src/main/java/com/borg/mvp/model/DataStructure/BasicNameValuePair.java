package com.borg.mvp.model.DataStructure;

import java.io.Serializable;
/**
 * Created by gulliver on 15/12/13.
 */
public class BasicNameValuePair implements NameValuePair, Cloneable, Serializable {
    private final String name;
    private final String value;

    /**
     * Default Constructor taking a name and a value. The value may be null.
     *
     * @param name The name.
     * @param value The value.
     */
    public BasicNameValuePair(final String name, final String value) {
        super();
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}