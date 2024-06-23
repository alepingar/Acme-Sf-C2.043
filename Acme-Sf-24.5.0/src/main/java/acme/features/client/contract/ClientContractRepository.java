
package acme.features.client.contract;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;
import acme.entities.projects.Project;
import acme.roles.client.Client;
import acme.systemConfiguration.SystemConfiguration;

@Repository
public interface ClientContractRepository extends AbstractRepository {

	@Query("SELECT c FROM Client c WHERE c.id = :id")
	Client findClientById(int id);

	@Query("SELECT c FROM Contract c WHERE c.client.id = :clientId")
	Collection<Contract> findManyContractsByClientId(int clientId);

	@Query("SELECT c FROM Contract c WHERE c.id = :id")
	Contract findContractById(int id);

	@Query("SELECT c FROM Contract c WHERE c.code = :code")
	Contract findContractByCode(String code);

	@Query("SELECT c FROM Contract c")
	Collection<Contract> findAllContracts();

	@Query("SELECT pl FROM ProgressLog pl WHERE pl.contract.id = ?1")
	Collection<ProgressLog> findProgressLogsByContractId(int id);

	@Query("SELECT p FROM Project p")
	Collection<Project> findAllProjects();

	@Query("SELECT p FROM Project p WHERE p.id = ?1")
	Project findProjectById(int id);

	@Query("SELECT s FROM SystemConfiguration s")
	List<SystemConfiguration> findSystemConfiguration();

	@Query("SELECT c FROM Contract c WHERE c.project.id = :projectId")
	Collection<Contract> findContractsByProjectId(int projectId);
}
