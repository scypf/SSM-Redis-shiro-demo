package com.yu.person.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yu.person.model.Person;
import com.yu.person.service.IPersonService;

@Controller
@RequestMapping("/personController")
public class PersonController {
    
    private IPersonService personService;
    
    public IPersonService getPersonService() {
        return personService;
    }

    @Autowired
    public void setPersonService(IPersonService personService) {
        this.personService = personService;
    }

    @RequestMapping("/showPerson")
    public String showPersons(Model model){
        List<Person> persons = personService.loadPersons();
        model.addAttribute("persons", persons);
        return "showpersonlist";
    }
    
    
    @RequestMapping("/getPersonById/{id}")
    public String getPersonById(@PathVariable  String id, Model model){
        Person person = personService.findPersonById(id);
        model.addAttribute("person", person);
        return "showoneperson";
    }

}
