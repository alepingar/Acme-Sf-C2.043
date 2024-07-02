
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contract.Contract;
import acme.roles.client.Client;

@Service
public class ClientContractListService extends AbstractService<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRole(Client.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Contract> objects;
		int clientId;

		clientId = super.getRequest().getPrincipal().getActiveRoleId();
		objects = this.repository.findManyContractsByClientId(clientId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Contract object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "providerName", "customerName", "draftMode");
		dataset.put("contractId", object.getId());
		super.getResponse().addData(dataset);
	}
}
