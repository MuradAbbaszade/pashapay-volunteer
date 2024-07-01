package az.m10.service;

import az.m10.domain.*;
import az.m10.dto.VolunteerDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final PasswordEncoder passwordEncoder;
    private final BaseJpaRepository<Volunteer, Long> repository;

    private final String PROFILE_IMAGE_PATH = "/src/main/resources/profile-images/";

    public VolunteerService(VolunteerRepository volunteerRepository, AuthorityRepository authorityRepository,
                            UserRepository userRepository, TeamLeaderRepository teamLeaderRepository, PasswordEncoder passwordEncoder,
                            BaseJpaRepository<Volunteer, Long> repository) {
        this.volunteerRepository = volunteerRepository;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Transactional
    public VolunteerDTO add(VolunteerDTO dto, MultipartFile profileImageFile) {
        userRepository.findByUsername(dto.getUsername()).ifPresent(account -> {
            throw new IllegalArgumentException("This username is already taken");
        });
        TeamLeader teamLeader = teamLeaderRepository.findById(dto.getTeamLeaderId()).orElseThrow(
                () -> new CustomNotFoundException("Team leader not found")
        );
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
        volunteer.setTeamLeader(teamLeader);
        volunteer.setProfileImage(saveProfileImage(profileImageFile));
        volunteer = volunteerRepository.save(volunteer);
        return volunteer.toDto();
    }

    public String saveProfileImage(MultipartFile profileImageFile) {
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            String fileExtension = profileImageFile.getOriginalFilename().substring(profileImageFile.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID() + fileExtension;
            try {
                Path folderPath = Paths.get(PROFILE_IMAGE_PATH);
                if (!Files.exists(folderPath)) {
                    Files.createDirectories(folderPath);
                }
                byte[] bytes = profileImageFile.getBytes();
                Path filePath = folderPath.resolve(fileName);
                Files.write(filePath, bytes);
                return filePath.toString();
            } catch (IOException e) {
                throw new IllegalArgumentException("An error occurred while saving profile image");
            }
        }
        return null;
    }

    public VolunteerDTO update(Long id, VolunteerDTO dto) {
        Volunteer volunteer = volunteerRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found.")
        );
        TeamLeader teamLeader = teamLeaderRepository.findById(dto.getTeamLeaderId()).orElseThrow(
                () -> new CustomNotFoundException("Entity not found.")
        );
        User user = volunteer.getUser();
        user.setUsername(dto.getUsername());
        volunteer = dto.toEntity(Optional.of(volunteer));
        volunteer.setTeamLeader(teamLeader);
        volunteerRepository.save(volunteer);
        return volunteer.toDto();
    }

    public User saveFcmToken(String username, String fcmToken) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomNotFoundException("User not found")
        );
        user.setFcmToken(fcmToken);
        return userRepository.save(user);
    }

    public VolunteerDTO findById(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found")
        );
        return volunteer.toDto();
    }

    public void delete(Long id) {
        volunteerRepository.deleteById(id);
    }

    public List<VolunteerDTO> findAll() {
        List<Volunteer> entities = volunteerRepository.findAll();
        List<VolunteerDTO> dtos = new ArrayList<>();
        for (Volunteer entity : entities) {
            dtos.add(entity.toDto());
        }
        return dtos;
    }
}
