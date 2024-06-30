
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
public class ClientProgressLogDeleteService extends AbstractService<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientProgressLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		ProgressLog progressLog;

		progressLog = this.repository.findProgressLogById(super.getRequest().getData("id", int.class));

		status = progressLog != null && progressLog.getDraftMode() && super.getRequest().getPrincipal().hasRole(progressLog.getContract().getClient());

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
	public void bind(final ProgressLog object) {
		assert object != null;
		super.bind(object, "recordId", "completenessPercentage", "progressComment", "registrationMoment", "responsiblePerson", "draftMode", "contract");

	}

	@Override
	public void validate(final ProgressLog object) {
		assert object != null;
	}

	@Override
	public void perform(final ProgressLog object) {
		assert object != null;
		this.repository.delete(object);
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
		dataset = super.unbind(object, "recordId", "completenessPercentage", "progressComment", "registrationMoment", "responsiblePerson");
		dataset.put("contract", choices.getSelected().getKey());
		dataset.put("contracts", choices);
		dataset.put("draftMode", object.getDraftMode());

		super.getResponse().addData(dataset);
	}

}
