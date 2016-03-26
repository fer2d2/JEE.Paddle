package web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import business.controllers.UserController;
import business.wrapper.UserWrapper;

@Controller
public class UserPresenter {

    @Autowired
    private UserController userController;

    public UserPresenter() {

    }

    @RequestMapping(value = WebUris.USERS + WebUris.ACTION_SHOW_ALL, method = RequestMethod.GET)
    public String showAllUsers(Model model) {
        model.addAttribute("users", userController.showUsers());
        return "showUsers";
    }

    @RequestMapping(value = WebUris.USERS + WebUris.ACTION_CREATE, method = RequestMethod.GET)
    public String createUser(Model model) {
        model.addAttribute("user", new UserWrapper());
        return "";
    }

    @RequestMapping(value = WebUris.USERS + WebUris.ACTION_CREATE, method = RequestMethod.POST)
    public String createUserFormData(@Valid UserWrapper user, BindingResult bindingResult, Model model) {
        return "";
    }

}
