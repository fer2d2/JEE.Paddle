package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomePresenter {

    public HomePresenter() {
        
    }
    
    @RequestMapping(value = WebUris.HOME, method = RequestMethod.GET)
    public String showHome() {
        return "home";
    }
}
