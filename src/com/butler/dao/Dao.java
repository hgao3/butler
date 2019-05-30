package com.butler.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
	
    T get(Integer id);
    
    List<T> getAll();
    
    void printAll();
     
    void save(T t);
     
    //void update(T t, String[] params);
     
    //void delete(T t);
}
