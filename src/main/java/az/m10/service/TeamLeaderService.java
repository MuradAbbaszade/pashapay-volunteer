package az.m10.service;

import az.m10.domain.*;
import az.m10.dto.TeamLeaderDTO;
import az.m10.dto.VolunteerDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.AuthorityRepository;
import az.m10.repository.BaseJpaRepository;
import az.m10.repository.TeamLeaderRepository;
import az.m10.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamLeaderService extends GenericService<TeamLeader, TeamLeaderDTO> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    private final TeamLeaderRepository teamLeaderRepository;

    public TeamLeaderService(BaseJpaRepository<TeamLeader, Long> repository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, TeamLeaderRepository teamLeaderRepository) {
        super(repository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.teamLeaderRepository = teamLeaderRepository;
    }

    public List<VolunteerDTO> findAllVolunteersByTeamLeaderId(Long teamLeaderId) {
        TeamLeader teamLeader = teamLeaderRepository.findById(teamLeaderId).orElseThrow(
                () -> new CustomNotFoundException("Team leader not found")
        );
        List<Volunteer> volunteers = teamLeader.getVolunteers();
        List<VolunteerDTO> volunteerDTOS = new ArrayList<>();
        for (Volunteer volunteer : volunteers) {
            volunteerDTOS.add(volunteer.toDto());
        }
        return volunteerDTOS;
    }

    @Override
    @Transactional
    public TeamLeader add(TeamLeaderDTO dto) {
        userRepository.findByUsername(dto.getUsername()).ifPresent(account -> {
            throw new IllegalArgumentException("This username is already taken");
        });
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Authority authority = authorityRepository.findByAuthority(UserAuthority.TEAM_LEADER.toString())
                .orElseGet(() -> {
                    Authority newAuthority = new Authority();
                    newAuthority.setAuthority(UserAuthority.TEAM_LEADER.toString());
                    return authorityRepository.save(newAuthority);
                });
        user.setAuthorities(Set.of(authority));
        user = userRepository.save(user);

        TeamLeader teamLeader = new TeamLeader();
        teamLeader.setUserId(user.getId());
        teamLeader.setUser(user);
        teamLeader = dto.toEntity(Optional.of(teamLeader));
        teamLeader = teamLeaderRepository.save(teamLeader);
        return teamLeader;
    }
}
