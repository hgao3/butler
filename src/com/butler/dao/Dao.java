package com.butler.dao;

import java.util.List;
import java.util.Optional;

import com.butler.exception.ResourceNotFoundException;

public interface Dao<T> {
    T get(Integer id) throws ResourceNotFoundException;
    List<T> getAll();
    void printAll();
    int save(T t);
    void update(Integer id, T t);
    void delete(Integer id);
}
