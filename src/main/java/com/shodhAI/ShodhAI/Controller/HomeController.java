package com.shodhAI.ShodhAI.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String HomePage(){
        return "HOME1";
    }

    @GetMapping("/dummy")
    public String DummyPage() {return "dummy";}
}

