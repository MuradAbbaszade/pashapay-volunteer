package az.m10.repository;

import az.m10.domain.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends BaseJpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE " +
            "(:subway IS NULL OR :subway = '' OR l.subway LIKE CONCAT(:subway, '%')) AND " +
            "(:district IS NULL OR :district = '' OR l.district = :district) AND " +
            "(:market IS NULL OR :market = '' OR l.market = :market)")
    List<Location> findBySubwayDistrictMarket(@Param("subway") String subway,
                                              @Param("district") String district,
                                              @Param("market") String market);
}
