
package acme.features.sponsor.sponsorship;

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
import acme.entities.projects.Project;
import acme.entities.sponsorships.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.sponsorships.SponsorshipType;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipPublishService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository repository;

	// AbstractService interface ---------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		Sponsor sponsor;
		Sponsorship sponsorship;

		id = super.getRequest().getData("id", int.class);
		sponsorship = this.repository.findOneSponsorshipById(id);

		sponsor = sponsorship == null ? null : sponsorship.getSponsor();
		status = sponsorship != null && !sponsorship.isPublished() && super.getRequest().getPrincipal().hasRole(sponsor);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Sponsorship object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneSponsorshipById(id);

		Date instantiationMoment;
		instantiationMoment = MomentHelper.getCurrentMoment();
		object.setMoment(instantiationMoment);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Sponsorship object) {
		assert object != null;
		super.bind(object, "code", "startDate", "endDate", "type", "amount", "email", "link", "project");

	}

	@Override
	public void validate(final Sponsorship object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("amount")) {
			Double amount = object.getAmount().getAmount();
			Double total = 0.0;
			boolean allPublished = true;
			Collection<Invoice> invoices = this.repository.findAllInvoicesBySponsorshipId(object.getId());
			for (Invoice invoice : invoices)
				if (invoice.isPublished())
					total += invoice.getValue().getAmount();
				else
					allPublished = false;

			super.state(amount.equals(total) && allPublished, "amount", "sponsor.sponsorship.form.error.invoices");

		}

		String dateString = "2201/01/01 00:00";
		Date futureMostDate = MomentHelper.parse(dateString, "yyyy/MM/dd HH:mm");
		dateString = "2200/12/1 00:00";
		Date latestStartDate = MomentHelper.parse(dateString, "yyyy/MM/dd HH:mm");

		String acceptedCurrencies = this.repository.findConfiguration().getAcceptedCurrencies();
		List<String> acceptedCurrencyList = Arrays.asList(acceptedCurrencies.split("\\s*,\\s*"));

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Sponsorship sponsorshipSameCode;
			sponsorshipSameCode = this.repository.findSponsorshipByCode(object.getCode());
			if (sponsorshipSameCode != null) {
				int id = sponsorshipSameCode.getId();
				super.state(id == object.getId(), "code", "sponsor.sponsorship.form.error.duplicate");
			}
		}

		if (object.getStartDate() != null) {

			if (!super.getBuffer().getErrors().hasErrors("startDate"))
				super.state(MomentHelper.isAfter(object.getStartDate(), object.getMoment()), "startDate", "sponsor.sponsorship.form.error.startDate");

			if (!super.getBuffer().getErrors().hasErrors("startDate"))
				super.state(MomentHelper.isBefore(object.getStartDate(), latestStartDate), "startDate", "sponsor.sponsorship.form.error.startDateLate");

			if (object.getEndDate() != null) {

				if (!super.getBuffer().getErrors().hasErrors("endDate"))
					super.state(MomentHelper.isAfter(object.getEndDate(), object.getMoment()), "endDate", "sponsor.sponsorship.form.error.endDate");

				if (!super.getBuffer().getErrors().hasErrors("startDate"))
					super.state(MomentHelper.isBefore(object.getStartDate(), object.getEndDate()), "startDate", "sponsor.sponsorship.form.error.startDateBeforeEndDate");

				if (!super.getBuffer().getErrors().hasErrors("endDate"))
					super.state(MomentHelper.isBefore(object.getEndDate(), futureMostDate), "endDate", "sponsor.sponsorship.form.error.dateLate");

				if (!super.getBuffer().getErrors().hasErrors("endDate"))
					super.state(MomentHelper.isLongEnough(object.getStartDate(), object.getEndDate(), 1, ChronoUnit.MONTHS), "endDate", "sponsor.sponsorship.form.error.period");

			}
		}

		if (object.getAmount() != null) {
			if (!super.getBuffer().getErrors().hasErrors("amount"))
				super.state(object.getAmount().getAmount() <= 1000000.00 && object.getAmount().getAmount() >= 0.00, "amount", "sponsor.sponsorship.form.error.amountTooHigh");

			if (!super.getBuffer().getErrors().hasErrors("amount"))
				super.state(this.repository.countPublishedInvoicesBySponsorshipId(object.getId()) == 0 || object.getAmount().getCurrency().equals(this.repository.findOneSponsorshipById(object.getId()).getAmount().getCurrency()), "amount",
					"sponsor.sponsorship.form.error.currencyChange");

			if (!super.getBuffer().getErrors().hasErrors("amount"))
				super.state(acceptedCurrencyList.contains(object.getAmount().getCurrency()), "amount", "sponsor.sponsorship.form.error.currencyNotSupported");
		}

	}

	@Override
	public void perform(final Sponsorship object) {
		assert object != null;
		object.setPublished(true);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Sponsorship object) {

		assert object != null;
		Dataset dataset;
		SelectChoices choices;
		SelectChoices projects;

		Collection<Project> unpublishedProjects = this.repository.findAllUnpublishedProjects();
		projects = SelectChoices.from(unpublishedProjects, "code", object.getProject());

		choices = SelectChoices.from(SponsorshipType.class, object.getType());

		dataset = super.unbind(object, "code", "moment", "startDate", "endDate", "type", "amount", "email", "link", "published");
		dataset.put("types", choices);

		dataset.put("project", projects.getSelected().getKey());
		dataset.put("projects", projects);

		super.getResponse().addData(dataset);
	}

}
