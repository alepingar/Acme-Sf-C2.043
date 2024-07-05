
package acme.features.developer.trainingModule;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.projects.Project;
import acme.entities.training.TrainingModule;
import acme.entities.training.TrainingSession;
import acme.roles.Developer;

public interface DeveloperTrainingModuleRepository extends AbstractRepository {

	//List the training modules that they have created.
	@Query("select tm from TrainingModule tm where tm.developer.id = ?1")
	Collection<TrainingModule> findManyByDeveloperId(int developerId);

	@Query("select tm from TrainingModule tm where tm.id = ?1")
	TrainingModule findTrainingModuleById(int id);

	@Query("select d from Developer d where d.id = ?1")
	Developer findDeveloperById(int id);

	@Query("select p from Project p")
	Collection<Project> findAllProject();

	@Query("select p from Project p where p.id = ?1")
	Project findProjectById(int id);

	@Query("select tm from TrainingModule tm where tm.code = ?1")
	TrainingModule findTrainingModuleByCode(String code);

	//find training session by training module id
	@Query("select ts from TrainingSession ts where ts.trainingModule.id = ?1")
	Collection<TrainingSession> findTrainingSessionByTrainingModuleId(int trainingModuleId);
}
