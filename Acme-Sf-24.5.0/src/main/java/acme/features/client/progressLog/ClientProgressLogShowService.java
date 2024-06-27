
package acme.features.client.progressLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;
import acme.roles.client.Client;

@Service
public class ClientProgressLogShowService extends AbstractService<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int progressLogId;
		Contract contract;

		progressLogId = super.getRequest().getData("id", int.class);
		contract = this.repository.findContractByProgressLogId(progressLogId);
		status = contract != null && super.getRequest().getPrincipal().hasRole(contract.getClient());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ProgressLog object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findProgressLogById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final ProgressLog object) {
		assert object != null;

		SelectChoices choices;
		Dataset dataset;
		int clientId;
		clientId = super.getRequest().getPrincipal().getActiveRoleId();
		final Collection<Contract> contracts = this.repository.findContractsByClientId(clientId);

		choices = SelectChoices.from(contracts, "code", object.getContract());
		dataset = super.unbind(object, "recordId", "completenessPercentage", "progressComment", "registrationMoment", "responsiblePerson", "draftMode");
		dataset.put("contract", choices.getSelected().getKey());
		dataset.put("contracts", choices);

		super.getResponse().addData(dataset);
	}
}
