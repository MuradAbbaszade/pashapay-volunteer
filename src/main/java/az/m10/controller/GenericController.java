package az.m10.controller;

import az.m10.domain.BaseEntity;
import az.m10.dto.BaseDTO;
import az.m10.service.GenericService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public abstract class GenericController<T extends BaseEntity, E extends BaseDTO> {

    GenericService<T, E> genericService;

    public GenericController(GenericService<T, E> genericService) {
        this.genericService = genericService;
    }

    @PostMapping
    public ResponseEntity<E> add(@RequestBody @Valid E dto) throws IOException {
        E e = genericService.add(dto);
        return ResponseEntity.ok(e);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            genericService.delete(id);
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
    public ResponseEntity<E> findById(@PathVariable Long id) {
        return ResponseEntity.ok(genericService.findById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<E> update(@PathVariable Long id, @Valid @RequestBody E dto) {
        E e = genericService.update(id, dto);
        return ResponseEntity.ok(e);
    }

    @GetMapping
    public List<E> findAll() {
        return genericService.findAll();
    }
}
