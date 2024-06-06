package az.m10.service;

import az.m10.domain.*;
import az.m10.dto.VolunteerDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.AuthorityRepository;
import az.m10.repository.BaseJpaRepository;
import az.m10.repository.UserRepository;
import az.m10.repository.VolunteerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VolunteerService extends GenericService<Volunteer, VolunteerDTO> {
    private final VolunteerRepository volunteerRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BaseJpaRepository<Volunteer, Long> repository;

    public VolunteerService(VolunteerRepository volunteerRepository, AuthorityRepository authorityRepository,
                            UserRepository userRepository, PasswordEncoder passwordEncoder,
                            BaseJpaRepository<Volunteer, Long> repository) {
        super(repository);
        this.volunteerRepository = volunteerRepository;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Transactional
    public Volunteer add(VolunteerDTO dto) {
        userRepository.findByUsername(dto.getUsername()).ifPresent(account -> {
            throw new IllegalArgumentException("This username is already taken");
        });
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Authority authority = authorityRepository.findByAuthority(UserAuthority.VOLUNTEER.toString())
                .orElseGet(() -> {
                    Authority newAuthority = new Authority();
                    newAuthority.setAuthority(UserAuthority.VOLUNTEER.toString());
                    return authorityRepository.save(newAuthority);
                });
        user.setAuthorities(Set.of(authority));
        user = userRepository.save(user);

        Volunteer volunteer = new Volunteer();
        volunteer.setUserId(user.getId());
        volunteer.setUser(user);
        volunteer = dto.toEntity(Optional.of(volunteer));
        volunteer = volunteerRepository.save(volunteer);
        return volunteer;
    }

    @Override
    public Volunteer update(Long id, VolunteerDTO dto) {
        Volunteer volunteer = volunteerRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found.")
        );
        User user = volunteer.getUser();
        user.setUsername(dto.getUsername());
        volunteer = dto.toEntity(Optional.of(volunteer));
        volunteerRepository.save(volunteer);
        return volunteer;
    }
}
