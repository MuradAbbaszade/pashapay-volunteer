package az.m10.controller;

import az.m10.dto.NotificationRequest;
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
    private final FCMService fcmService;

    public VolunteerController(VolunteerService volunteerService, FCMService fcmService) {
        this.volunteerService = volunteerService;
        this.fcmService = fcmService;
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
        fcmService.sendMessageToToken(new NotificationRequest(
                "Rezervasiya başlayır",
                "Rezervasiyanın başlaması üçün 15 dəqiqə ərzində təsdiq edin",
                "Rezervasiyanın başlaması üçün 15 dəqiqə ərzində təsdiq edin",
                "cMDJA7TIREK-4zto1JkgXT:APA91bG567w9ibZceNDVW0QiIVB3mZDbqcWy6JfAP056WXIQVZ1ST_3BnmbJm_E7mf3o0hOWSz7GnWFnHmYsq5jSe73yzpUV-xEesYJA4bt74ISlC2YmbSO1B3LV2wFPDZp52YwjV9l4"
        ));
        return volunteerService.findAll();
    }
}
