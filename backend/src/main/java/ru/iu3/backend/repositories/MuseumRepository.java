package ru.iu3.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.Country;
import java.util.Optional;
public interface MuseumRepository extends JpaRepository<Museum, Long> {
	Optional<Museum> findByName(String name);
}