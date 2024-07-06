
package acme.features.any.contract;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contract.Contract;
import acme.entities.projects.Project;

@Repository
public interface AnyContractRepository extends AbstractRepository {

	@Query("SELECT c FROM Contract c WHERE c.draftMode = false")
	Collection<Contract> findAllPublishedContracts();

	@Query("SELECT c FROM Contract c WHERE c.id = :id")
	Contract findContractById(int id);

	@Query("SELECT p FROM Project p")
	Collection<Project> findAllProjects();
}
