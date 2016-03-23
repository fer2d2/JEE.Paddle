package business.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import business.controllers.AdminController;

@RestController
@RequestMapping(Uris.SERVLET_MAP + Uris.ADMINS)
public class AdminResource {

    private AdminController adminController;

    @Autowired
    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteAllExceptAdmin() {
        adminController.deleteAllExceptAdmin();
    }

}
