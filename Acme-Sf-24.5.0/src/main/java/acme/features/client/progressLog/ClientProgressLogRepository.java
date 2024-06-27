
package acme.features.client.progressLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;
import acme.roles.client.Client;

@Repository
public interface ClientProgressLogRepository extends AbstractRepository {

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.contract.client.id = ?1")
	Collection<ProgressLog> findProgressLogsByClientId(int id);

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.contract.id = :contractId")
	Collection<ProgressLog> findProgressLogsByContractId(int contractId);

	@Query("SELECT pl.contract FROM ProgressLog pl WHERE pl.id = :id")
	Contract findContractByProgressLogId(int id);

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.id = :id")
	ProgressLog findProgressLogById(int id);

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.recordId = :recordId")
	ProgressLog findProgressLogByRecordId(String recordId);

	@Query("SELECT c FROM Contract c WHERE c.id = :contractId")
	Contract findContractById(int contractId);

	@Query("SELECT cl FROM Client cl WHERE cl.id = :clientId")
	Client findClientById(int clientId);

	@Query("SELECT max(pl.completenessPercentage) FROM ProgressLog pl WHERE pl.contract.id = :contractId and pl.draftMode = false")
	Double findPublishedProgressLogWithMaxCompleteness(int contractId);

	@Query("SELECT c FROM Contract c WHERE c.client.id = :clientId")
	Collection<Contract> findContractsByClientId(int clientId);

	@Query("SELECT c FROM Contract c")
	Collection<Contract> findAllContracts();
}
