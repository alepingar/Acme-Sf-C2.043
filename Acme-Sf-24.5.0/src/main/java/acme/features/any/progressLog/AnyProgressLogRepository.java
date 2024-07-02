
package acme.features.any.progressLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;

@Repository
public interface AnyProgressLogRepository extends AbstractRepository {

	@Query("SELECT c FROM Contract c WHERE c.id = :id")
	Contract findContractById(int id);

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.id = :id")
	ProgressLog findProgressLogById(int id);

	@Query("SELECT pl.contract FROM ProgressLog pl WHERE pl.id = :id")
	Contract findContractByProgressLogId(int id);

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.contract.id = :masterId AND pl.draftMode = false")
	Collection<ProgressLog> findPublishedProgressLogByMasterId(int masterId);
}
