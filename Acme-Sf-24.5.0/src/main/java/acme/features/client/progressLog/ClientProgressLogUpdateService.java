
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
public class ClientProgressLogUpdateService extends AbstractService<Client, ProgressLog> {

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
		int contractId;
		Contract contract;

		contractId = super.getRequest().getData("contract", int.class);
		contract = this.repository.findContractById(contractId);

		super.bind(object, "recordId", "completenessPercentage", "progressComment", "registrationMoment", "responsiblePerson", "draftMode");
		object.setContract(contract);

	}

	@Override
	public void validate(final ProgressLog object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("recordId")) {
			ProgressLog existing;

			existing = this.repository.findProgressLogByRecordId(object.getRecordId());
			super.state(existing == null || existing.equals(object), "recordId", "client.progress-log.form.error.duplicated");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationMoment"))
			super.state(object.getRegistrationMoment().after(object.getContract().getInstantiationMoment()), "registrationMoment", "client.progress-log.form.error.registration-moment-must-be-later");

	}

	@Override
	public void perform(final ProgressLog object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final ProgressLog object) {

		assert object != null;

		Collection<Contract> contracts;
		SelectChoices choices;

		contracts = this.repository.findAllContracts();
		choices = SelectChoices.from(contracts, "code", object.getContract());

		Dataset dataset;

		dataset = super.unbind(object, "recordId", "completenessPercentage", "progressComment", "registrationMoment", "responsiblePerson", "draftMode");
		dataset.put("contract", choices.getSelected().getKey());
		dataset.put("contracts", choices);

		super.getResponse().addData(dataset);
	}

}
