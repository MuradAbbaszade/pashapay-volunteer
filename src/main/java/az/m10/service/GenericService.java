package az.m10.service;

import az.m10.domain.BaseEntity;
import az.m10.dto.BaseDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.BaseJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public abstract class GenericService<T extends BaseEntity, E extends BaseDTO> {
    private final BaseJpaRepository<T, Long> baseJpaRepository;

    public GenericService(BaseJpaRepository<T, Long> repository) {
        this.baseJpaRepository = repository;
    }

    @Transactional
    public abstract E add(E dto);

    public E update(Long id, E dto) {
        T t = baseJpaRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found.")
        );
        t = (T) dto.toEntity(Optional.of(t));
        return (E) baseJpaRepository.save(t).toDto();
    }

    public E findById(Long id) {
        T t =  baseJpaRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Entity not found")
        );
        return (E) t.toDto();
    }

    public void delete(Long id) {
        baseJpaRepository.deleteById(id);
    }

    public List<E> findAll()
    {
        List<T> entities = baseJpaRepository.findAll();
        List<E> dtos = new ArrayList<>();
        for (T entity : entities){
            dtos.add((E) entity.toDto());
        }
        return dtos;
    }
}
