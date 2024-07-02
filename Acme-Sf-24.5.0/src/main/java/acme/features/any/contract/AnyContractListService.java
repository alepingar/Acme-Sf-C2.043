
package acme.features.any.contract;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contract.Contract;

@Service
public class AnyContractListService extends AbstractService<Any, Contract> {

	@Autowired
	private AnyContractRepository repository;


	@Override
	public void authorise() {
		final boolean status = true;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Contract> objects;

		objects = this.repository.findAllPublishedContracts();
		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Contract object) {
		assert object != null;

		Dataset dataset;
		dataset = super.unbind(object, "code", "providerName", "customerName", "draftMode");
		super.getResponse().addData(dataset);
	}

}
