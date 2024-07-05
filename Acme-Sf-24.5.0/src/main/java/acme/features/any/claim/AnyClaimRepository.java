
package acme.features.any.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;

public interface AnyClaimRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c")
	Collection<Claim> findAllPublishedClaims();

	@Query("SELECT c FROM Claim c WHERE c.id = :id")
	Claim findClaimById(int id);

}
