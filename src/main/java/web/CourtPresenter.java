package web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import business.controllers.CourtController;
import business.wrapper.CourtState;

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

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_CREATE, method = RequestMethod.GET)
    public String createCourt(Model model) {
        int courtId = courtController.showCourts().size() + 1;

        Map<Boolean,String> activeSelect = new LinkedHashMap<Boolean,String>();
        activeSelect.put(true, "Activa");
        activeSelect.put(false, "Inactiva");
        
        model.addAttribute("court", new CourtState(courtId, true));
        model.addAttribute("activeOptions", activeSelect);
        
        return "createCourt";
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_CREATE, method = RequestMethod.POST)
    public String createCourtFormData(Model model, @ModelAttribute(value = "court") CourtState court, BindingResult bindingResult) {
        courtController.createCourt(court.getCourtId(), court.isActive());
        this.createCourt(model);
        return "createCourt";
    }
}
