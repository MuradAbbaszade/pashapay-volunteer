package az.m10.controller;

import az.m10.domain.TeamLeader;
import az.m10.dto.TeamLeaderDTO;
import az.m10.dto.VolunteerDTO;
import az.m10.service.GenericService;
import az.m10.service.TeamLeaderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/team-leader")
@CrossOrigin(origins = "*")
public class TeamLeaderController extends GenericController<TeamLeader, TeamLeaderDTO> {

    private final TeamLeaderService teamLeaderService;
    public TeamLeaderController(GenericService<TeamLeader, TeamLeaderDTO> genericService, TeamLeaderService teamLeaderService) {
        super(genericService);
        this.teamLeaderService = teamLeaderService;
    }

    @GetMapping("/volunteers/{teamLeaderId}")
    public List<VolunteerDTO> findAllVolunteerByTeamLeaderId(@PathVariable Long teamLeaderId){
        return teamLeaderService.findAllVolunteersByTeamLeaderId(teamLeaderId);
    }
}
