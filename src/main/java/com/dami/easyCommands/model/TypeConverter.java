package com.dami.easyCommands.model;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(String s) throws Exception;
}
