
package acme.features.sponsor.sponsorDashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.forms.SponsorDashboard;
import acme.roles.Sponsor;

@Service
public class SponsorDashboardShowService extends AbstractService<Sponsor, SponsorDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		String defaultCurrency = this.repository.findConfiguration().getDefaultCurrency();

		int totalNumberInvoicesTaxEqualOrLessThan21 = this.repository.countInvoicesWithTaxLessThanOrEqual21(sponsorId);
		int totalNumberSponsorshipsWithLink = this.repository.countSponsorshipsWithLink(sponsorId);

		Double averageQuantityInvoices = this.repository.averageQuantityInvoices(sponsorId, defaultCurrency);
		Double stdevQuantityInvoices = this.repository.stdevQuantityInvoices(sponsorId, defaultCurrency);
		Double minimumQuantityInvoices = this.repository.minimumQuantityInvoices(sponsorId, defaultCurrency);
		Double maximumQuantityInvoices = this.repository.maximumQuantityInvoices(sponsorId, defaultCurrency);

		Double averageAmountSponsorships = this.repository.averageAmountSponsorships(sponsorId, defaultCurrency);
		Double stdevAmountSponsorships = this.repository.stdevAmountSponsorships(sponsorId, defaultCurrency);
		Double minimumAmountSponsorships = this.repository.minimumAmountSponsorships(sponsorId, defaultCurrency);
		Double maximumAmountSponsorships = this.repository.maximumAmountSponsorships(sponsorId, defaultCurrency);

		SponsorDashboard dashboard = new SponsorDashboard();

		dashboard.setTotalNumberInvoicesTaxEqualOrLessThan21(totalNumberInvoicesTaxEqualOrLessThan21);
		dashboard.setTotalNumberSponsorshipsWithLink(totalNumberSponsorshipsWithLink);

		dashboard.setAverageQuantityInvoices(averageQuantityInvoices);
		dashboard.setStdevQuantityInvoices(stdevQuantityInvoices);
		dashboard.setMinimumQuantityInvoices(minimumQuantityInvoices);
		dashboard.setMaximumQuantityInvoices(maximumQuantityInvoices);

		dashboard.setAverageAmountSponsorships(averageAmountSponsorships);
		dashboard.setStdevAmountSponsorships(stdevAmountSponsorships);
		dashboard.setMinimumAmountSponsorships(minimumAmountSponsorships);
		dashboard.setMaximumAmountSponsorships(maximumAmountSponsorships);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final SponsorDashboard object) {
		assert object != null;

		Dataset dataset = super.unbind(object, //
			"totalNumberInvoicesTaxEqualOrLessThan21", "totalNumberSponsorshipsWithLink", //
			"averageAmountSponsorships", "stdevAmountSponsorships", //
			"minimumAmountSponsorships", "maximumAmountSponsorships", //
			"averageQuantityInvoices", "stdevQuantityInvoices", //
			"minimumQuantityInvoices", "maximumQuantityInvoices");

		super.getResponse().addData(dataset);
	}
}
