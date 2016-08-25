package com.yu.person.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yu.common.annotation.RedisCache;
import com.yu.person.dao.PersonMapper;
import com.yu.person.model.Person;
import com.yu.person.service.IPersonService;

@Service("personService")
public class PersonServiceImpl implements IPersonService {
    
    private PersonMapper personMapper;

    public PersonMapper getPersonMapper() {
        return personMapper;
    }
    @Autowired
    public void setPersonMapper(PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    @Override
    public List<Person> loadPersons() {
        return personMapper.queryAll();
    }
    
    @Override
    @RedisCache(type = Person.class, expire = 3600)  //单位：秒  
    public Person findPersonById(String id) {
        return personMapper.findPersonById(Integer.parseInt(id));
    }
    
}