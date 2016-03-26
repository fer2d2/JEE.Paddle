package web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import business.controllers.CourtController;

@Controller
public class CourtPresenter {

    @Autowired
    private CourtController courtController;

    public CourtPresenter() {

    }

    @RequestMapping(WebUris.HOME)
    public String home(Model model) {
        return "/home";
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_SHOW_ALL, method = RequestMethod.GET)
    public String showAllCourts(Model model) {
        model.addAttribute("courts", courtController.showCourts());
        return "/showCourts";
    }
}
