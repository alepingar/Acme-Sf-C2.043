
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contract.Contract;
import acme.entities.projects.Project;
import acme.roles.client.Client;

@Service
public class ClientContractShowService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Contract contract;

		contract = this.repository.findContractById(super.getRequest().getData("id", int.class));

		status = contract != null && super.getRequest().getPrincipal().hasRole(contract.getClient());

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

		Collection<Project> projects;
		SelectChoices choices;

		projects = this.repository.findAllProjects();
		choices = SelectChoices.from(projects, "code", object.getProject());

		Dataset dataset;

		dataset = super.unbind(object, "code", "instantiationMoment", "providerName", "customerName", "goals", "budget", "draftMode");
		dataset.put("project", choices.getSelected().getKey());
		dataset.put("projects", choices);

		super.getResponse().addData(dataset);
	}

}
