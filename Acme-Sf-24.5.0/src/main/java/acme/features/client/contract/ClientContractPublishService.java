
package acme.features.client.contract;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contract.Contract;
import acme.entities.projects.Project;
import acme.roles.client.Client;
import acme.systemConfiguration.SystemConfiguration;

@Service
public class ClientContractPublishService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Contract contract;

		contract = this.repository.findContractById(super.getRequest().getData("id", int.class));
		status = contract != null && contract.getDraftMode() && super.getRequest().getPrincipal().hasRole(contract.getClient());

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
	public void bind(final Contract object) {
		assert object != null;
		int projectId;
		Project project;

		projectId = super.getRequest().getData("project", int.class);
		project = this.repository.findProjectById(projectId);

		super.bind(object, "code", "instantiationMoment", "providerName", "customerName", "goals", "budget", "draftMode");
		object.setProject(project);
	}

	@Override
	public void validate(final Contract object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Contract existing;

			existing = this.repository.findContractByCode(object.getCode());
			super.state(existing == null || existing.equals(object), "code", "client.contract.form.error.duplicated");

			if (!super.getBuffer().getErrors().hasErrors("budget")) {
				super.state(this.checkContractsAmountsLessThanProjectCost(object), "budget", "client.contract.form.error.exceededBudget");
				super.state(object.getBudget().getAmount() > 0, "budget", "client.contract.form.error.negative-amount");

				List<SystemConfiguration> sc = this.repository.findSystemConfiguration();
				final boolean foundCurrency = Stream.of(sc.get(0).aceptedCurrencies.split(",")).anyMatch(c -> c.equals(object.getBudget().getCurrency()));

				super.state(foundCurrency, "budget", "client.contract.form.error.currency-not-suported");

			}
		}

	}

	private Boolean checkContractsAmountsLessThanProjectCost(final Contract object) {
		assert object != null;

		Collection<Contract> contratos = this.repository.findContractsByProjectId(object.getProject().getId());

		Double budgetTotal = contratos.stream().filter(contract -> !contract.getDraftMode()).mapToDouble(contract -> contract.getBudget().getAmount()).sum();

		Double projectCost = object.getProject().getCost().getAmount();

		return projectCost >= budgetTotal + object.getBudget().getAmount();

	}

	@Override
	public void perform(final Contract object) {
		assert object != null;
		object.setDraftMode(false);
		this.repository.save(object);
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
