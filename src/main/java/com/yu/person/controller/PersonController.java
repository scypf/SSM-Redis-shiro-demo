package com.yu.person.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yu.person.model.Person;
import com.yu.person.service.IPersonService;

import dubbo.provider.service.TestService;



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

    //Dubbo消费端demo
    @Autowired
	private TestService testService;
	
	public TestService getTestService() {
		return testService;
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	@RequestMapping("/dubboConsumer")
	public String index(Model model){
		System.out.println("消费者:开始尝试调用提供者服务:sayHello");
	    testService.sayHello("pengfei");
	    System.out.println("消费者:提供者的服务:sayHello调用完毕");
		return "index";
	}
	
}
