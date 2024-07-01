package az.m10.controller;

import az.m10.dto.VolunteerDTO;
import az.m10.service.VolunteerService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/volunteer")
@CrossOrigin(origins = {"http://localhost:3000", "*"})
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<VolunteerDTO> add(@RequestPart @Valid VolunteerDTO dto, @ModelAttribute MultipartFile profileImage) throws IOException {
        VolunteerDTO e = volunteerService.add(dto, profileImage);
        return ResponseEntity.ok(e);
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
    public ResponseEntity<VolunteerDTO> update(@PathVariable Long id, @Valid @RequestBody VolunteerDTO dto) {
        VolunteerDTO e = volunteerService.update(id, dto);
        return ResponseEntity.ok(e);
    }

    @GetMapping
    public List<VolunteerDTO> findAll() {
        return volunteerService.findAll();
    }
}
