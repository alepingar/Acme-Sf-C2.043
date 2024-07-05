
package acme.features.administrator.banner;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.banner.Banner;

public interface AdministratorBannerRepository extends AbstractRepository {

	@Query("select b from Banner b where b.id = ?1")
	Banner findOneBannerById(int id);

	@Query("select b from Banner b")
	Collection<Banner> findAllBanner();
}
