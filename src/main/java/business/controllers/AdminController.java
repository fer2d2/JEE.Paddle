package business.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import data.services.DataService;

@Controller
public class AdminController {

    private DataService genericService;

    @Autowired
    public void setGenericService(DataService genericService) {
        this.genericService = genericService;
    }

    public void deleteAllExceptAdmin() {
        genericService.deleteAllExceptAdmin();
    }

}
