
package acme.features.any.claim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Any;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.claim.Claim;

@Service
public class AnyClaimCreateService extends AbstractService<Any, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyClaimRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim object;

		object = new Claim();
		object.setInstantiation(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Claim object) {
		assert object != null;

		super.bind(object, "code", "instantiation", "heading", "description", "departament", "email", "link");
		object.setInstantiation(MomentHelper.getCurrentMoment());

	}

	@Override
	public void validate(final Claim object) {
		assert object != null;
		final Boolean isConfirmed = super.getRequest().getData("confirm", Boolean.class);
		super.state(isConfirmed, "confirm", "any.claim.form.error.confirm");
	}

	@Override
	public void perform(final Claim object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		assert object != null;

		Dataset dataset;
		dataset = super.unbind(object, "code", "instantiation", "heading", "description", "departament", "email", "link");
		dataset.put("confirm", false);
		super.getResponse().addData(dataset);
	}

}
