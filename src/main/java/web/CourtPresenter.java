package web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import business.controllers.CourtController;
import business.wrapper.CourtState;

@Controller
public class CourtPresenter {

    @Autowired
    private CourtController courtController;

    public CourtPresenter() {

    }

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_SHOW_ALL, method = RequestMethod.GET)
    public String showAllCourts(Model model) {
        model.addAttribute("courts", courtController.showCourts());
        return "showCourts";
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_CREATE, method = RequestMethod.GET)
    public String createCourt(Model model) {
        int courtId = courtController.showCourts().size() + 1;

        Map<Boolean, String> activeSelect = new LinkedHashMap<Boolean, String>();
        activeSelect.put(true, "Activa");
        activeSelect.put(false, "Inactiva");

        model.addAttribute("court", new CourtState(courtId, true));
        model.addAttribute("activeOptions", activeSelect);

        return "createCourt";
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.ACTION_CREATE, method = RequestMethod.POST)
    public String createCourtFormData(Model model, @ModelAttribute(value = "court") CourtState court, BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (courtController.createCourt(court.getCourtId(), court.isActive())) {
            redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Pista dada de alta correctamente");
            return "redirect:" + WebUris.COURTS + WebUris.ACTION_CREATE;
        } else {
            bindingResult.rejectValue("courtId", "error.courtId", "La pista ya existe");
        }

        return "createCourt";
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.COURT_ACTIVE + WebUris.ID, method = RequestMethod.GET)
    public ModelAndView setCourtActive(@PathVariable int id, Model model) {
        courtController.changeCourtActivation(id, true);
        ModelAndView modelAndView = new ModelAndView("showCourts");
        modelAndView.addObject("courts", courtController.showCourts());
        return modelAndView;
    }

    @RequestMapping(value = WebUris.COURTS + WebUris.COURT_INACTIVE + WebUris.ID, method = RequestMethod.GET)
    public ModelAndView setCourtInactive(@PathVariable int id, Model model) {
        courtController.changeCourtActivation(id, false);
        ModelAndView modelAndView = new ModelAndView("showCourts");
        modelAndView.addObject("courts", courtController.showCourts());
        return modelAndView;
    }
}
