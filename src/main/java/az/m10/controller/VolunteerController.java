package az.m10.controller;

import az.m10.domain.Volunteer;
import az.m10.dto.VolunteerDTO;
import az.m10.service.GenericService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/volunteer")
@CrossOrigin(origins = "http://localhost:3000, https://adminve3.vercel.app")
public class VolunteerController extends GenericController<Volunteer, VolunteerDTO> {

    public VolunteerController(GenericService<Volunteer, VolunteerDTO> genericService) {
        super(genericService);
    }
}
