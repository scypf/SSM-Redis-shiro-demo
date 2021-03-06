package com.yu.person.service;

import java.util.List;

import com.yu.person.model.Person;

public interface IPersonService {

    /**
     * 加载全部的person
     * @return
     */
    List<Person> loadPersons();
    
    /**
     * 查询单个person
     * @return
     */
    Person findPersonById(String id);
}