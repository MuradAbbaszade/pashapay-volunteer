package az.m10.controller;

import az.m10.dto.ProfileImageDTO;
import az.m10.dto.VolunteerDTO;
import az.m10.service.FCMService;
import az.m10.service.VolunteerService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/admin/volunteer")
@CrossOrigin(origins = {"http://localhost:3000", "*"})
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<VolunteerDTO> add(@Valid @RequestPart VolunteerDTO dto,
                                            @ModelAttribute ProfileImageDTO profileImageDTO) throws IOException {
        VolunteerDTO createdVolunteer = volunteerService.add(dto, profileImageDTO.getProfileImage());
        return ResponseEntity.ok(createdVolunteer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            volunteerService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AccessDeniedException exp) {
            throw new AccessDeniedException(exp.getMessage());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<VolunteerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.findById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<VolunteerDTO> update(@PathVariable Long id, @Valid @RequestPart VolunteerDTO dto,
                                               @ModelAttribute ProfileImageDTO profileImageDTO) {
        VolunteerDTO e = volunteerService.update(id, dto, profileImageDTO.getProfileImage());
        return ResponseEntity.ok(e);
    }

    @GetMapping
    public List<VolunteerDTO> findAll() throws ExecutionException, InterruptedException {
        return volunteerService.findAll();
    }
}
