package com.yu.person.dao;

import java.util.List;

import com.yu.person.model.Person;

public interface PersonMapper {
    /**
     * 插入一条记录
     * @param person
     */
    void insert(Person person);
    
    /**
     * 查询所有
     * @return
     */
    List<Person> queryAll();
    
    Person findPersonById(int id);
}