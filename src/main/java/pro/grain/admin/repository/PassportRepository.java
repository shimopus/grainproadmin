package pro.grain.admin.repository;

import pro.grain.admin.domain.Passport;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Passport entity.
 */
@SuppressWarnings("unused")
public interface PassportRepository extends JpaRepository<Passport,Long> {

}
