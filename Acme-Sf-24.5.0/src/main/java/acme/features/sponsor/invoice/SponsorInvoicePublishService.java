
package acme.features.sponsor.invoice;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.sponsorships.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.roles.Sponsor;

@Service
public class SponsorInvoicePublishService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository repository;

	// AbstractService interface ---------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		Sponsor sponsor;
		Invoice invoice;

		id = super.getRequest().getData("id", int.class);
		invoice = this.repository.findOneInvoiceById(id);

		sponsor = invoice == null ? null : invoice.getSponsorship().getSponsor();
		status = invoice != null && !invoice.isPublished() && super.getRequest().getPrincipal().hasRole(sponsor);

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {

		Invoice object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneInvoiceById(id);

		Date moment;
		moment = MomentHelper.getCurrentMoment();
		object.setRegistrationTime(moment);

		object.setPublished(false);

		super.getBuffer().addData(object);

	}

	@Override
	public void bind(final Invoice object) {
		assert object != null;
		super.bind(object, "code", "link", "dueDate", "quantity", "tax", "sponsorship");

	}

	@Override
	public void validate(final Invoice object) {

		assert object != null;
		double total = 0.0;

		if (!super.getBuffer().getErrors().hasErrors("quantity")) {
			Sponsorship sponsorship = object.getSponsorship();

			if (sponsorship != null) {
				Collection<Invoice> invoices = this.repository.findAllInvoicesBySponsorshipId(sponsorship.getId());
				for (Invoice invoice : invoices)
					if (invoice.isPublished())
						total += invoice.getValue().getAmount();

				super.state(total + object.getValue().getAmount() <= object.getSponsorship().getAmount().getAmount(), "sponsorship", "invoice.sponsorship.form.error.amount");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("quantity"))
			super.state(object.getSponsorship() != null && object.getQuantity().getCurrency().equals(object.getSponsorship().getAmount().getCurrency()), "quantity", "sponsor.invoice.form.error.currency");

		String dateString = "2201/01/01 00:00";
		Date futureDate = MomentHelper.parse(dateString, "yyyy/MM/dd HH:mm");
		String acceptedCurrencies = this.repository.findConfiguration().getAcceptedCurrencies();
		List<String> acceptedCurrencyList = Arrays.asList(acceptedCurrencies.split("\\s*,\\s*"));

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Invoice invoiceSameCode;
			invoiceSameCode = this.repository.findInvoiceByCode(object.getCode());
			if (invoiceSameCode != null) {
				int id = invoiceSameCode.getId();
				super.state(id == object.getId(), "code", "sponsor.invoice.form.error.duplicate");
			}
		}

		if (object.getDueDate() != null) {

			if (!super.getBuffer().getErrors().hasErrors("dueDate"))
				super.state(MomentHelper.isAfter(object.getDueDate(), object.getRegistrationTime()), "dueDate", "sponsor.invoice.form.error.dueDate");

			if (!super.getBuffer().getErrors().hasErrors("dueDate"))
				super.state(MomentHelper.isBefore(object.getDueDate(), futureDate), "dueDate", "sponsor.invoice.form.error.dateLate");

			if (!super.getBuffer().getErrors().hasErrors("dueDate"))
				super.state(MomentHelper.isLongEnough(object.getRegistrationTime(), object.getDueDate(), 1, ChronoUnit.MONTHS), "dueDate", "sponsor.invoice.form.error.period");
		}

		if (!super.getBuffer().getErrors().hasErrors("sponsorship"))
			super.state(object.getSponsorship() != null && object.getSponsorship().isPublished() == false, "sponsorship", "sponsor.invoice.form.error.sponsorship");

		if (!super.getBuffer().getErrors().hasErrors("quanitity"))
			super.state(object.getQuantity() != null && object.getQuantity().getAmount() <= 1000000.00 && object.getQuantity().getAmount() >= 0.00, "quantity", "sponsor.invoice.form.error.amountTooHigh");

		if (!super.getBuffer().getErrors().hasErrors("quanitity"))
			super.state(object.getQuantity() != null && acceptedCurrencyList.contains(object.getQuantity().getCurrency()), "quantity", "sponsor.invoice.form.error.currencyNotAllowed");

		if (!super.getBuffer().getErrors().hasErrors("quantity"))
			super.state(object.getQuantity() != null && object.getSponsorship() != null && object.getQuantity().getCurrency().equals(object.getSponsorship().getAmount().getCurrency()), "quantity", "sponsor.invoice.form.error.currency");

	}

	@Override
	public void perform(final Invoice object) {

		assert object != null;
		object.setPublished(true);
		this.repository.save(object);

	}

	@Override
	public void unbind(final Invoice object) {

		assert object != null;

		Dataset dataset;
		SelectChoices sponsorships;
		int sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		Collection<Sponsorship> sponsorSponsorships = this.repository.findSponsorshipBySponsorId(sponsorId);
		sponsorships = SelectChoices.from(sponsorSponsorships, "code", object.getSponsorship());

		dataset = super.unbind(object, "code", "link", "registrationTime", "dueDate", "quantity", "tax", "published");
		Sponsorship selectedSponsorship = this.repository.findOneSponsorshipById(Integer.valueOf(sponsorships.getSelected().getKey()));
		dataset.put("sponsorship", sponsorships.getSelected().getKey());

		sponsorSponsorships = this.repository.findSponsorUnpublishedSponsorship(sponsorId);
		if (!sponsorSponsorships.contains(selectedSponsorship) && selectedSponsorship != null)
			sponsorSponsorships.add(selectedSponsorship);

		sponsorships = SelectChoices.from(sponsorSponsorships, "code", object.getSponsorship());
		dataset.put("sponsorships", sponsorships);

		super.getResponse().addData(dataset);

	}

}
