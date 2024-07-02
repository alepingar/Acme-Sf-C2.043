
package acme.features.client.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;
import acme.systemConfiguration.SystemConfiguration;

@Repository
public interface ClientDashboardRepository extends AbstractRepository {

	@Query("SELECT pl FROM ProgressLog pl")
	Collection<ProgressLog> findAllProgressLogs();

	@Query("SELECT c FROM Contract c")
	Collection<Contract> findAllContracts();

	@Query("SELECT c FROM Contract c WHERE c.client.id = :clientId")
	Collection<Contract> findContractsByClientId(int clientId);

	@Query("SELECT c.budget FROM Contract c WHERE c.client.id = :clientId AND c.draftMode = false")
	Collection<Money> findBudgetsByClientId(int clientId);

	@Query("SELECT s FROM SystemConfiguration s")
	List<SystemConfiguration> findSystemConfiguration();
}
