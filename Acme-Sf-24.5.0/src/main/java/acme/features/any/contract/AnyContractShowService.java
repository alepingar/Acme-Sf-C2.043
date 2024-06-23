
package acme.features.any.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.accounts.Authenticated;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contract.Contract;
import acme.entities.projects.Project;

@Service
public class AnyContractShowService extends AbstractService<Any, Contract> {

	@Autowired
	private AnyContractRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		final Contract contract;

		id = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractById(id);
		status = contract != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Contract object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findContractById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Contract object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choices;
		final Collection<Project> projects = this.repository.findAllProjects();

		choices = SelectChoices.from(projects, "code", object.getProject());

		dataset = super.unbind(object, "code", "instantiationMoment", "providerName", "customerName", "goals", "budget", "draftMode");
		dataset.put("authenticated", super.getRequest().getPrincipal().hasRole(Authenticated.class));
		dataset.put("project", choices.getSelected().getKey());
		dataset.put("projects", choices);
		super.getResponse().addData(dataset);
	}

}
