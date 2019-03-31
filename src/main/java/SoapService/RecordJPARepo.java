package SoapService;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa интерфейс для обслуживания сущности Record
 */
public interface RecordJPARepo extends JpaRepository <Record, Integer> {
}
