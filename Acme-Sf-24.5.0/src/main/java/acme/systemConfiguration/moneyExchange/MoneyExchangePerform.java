
package acme.systemConfiguration.moneyExchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.invoice.Invoice;

@Component
public class MoneyExchangePerform {

	@Autowired
	private MoneyExchangeRepository repository;


	public double totalMoneyExchangeInvoices(final Collection<Invoice> invoices, final String currencyTarget) {
		MoneyExchange lastMoneyExchange;
		Date currentDate;
		Double rate;
		BigDecimal temp;

		double accumulatedAmountInvoices = 00.00;

		if (invoices.isEmpty())
			return accumulatedAmountInvoices;
		else {
			for (Invoice i : invoices)
				if (i.getQuantity().getCurrency().equals(currencyTarget))
					accumulatedAmountInvoices += i.totalAmount();
				else {
					lastMoneyExchange = this.repository.findLastMoneyExchangeForCurrency(i.getQuantity().getCurrency(), currencyTarget);
					currentDate = MomentHelper.getCurrentMoment();

					if (lastMoneyExchange != null && MomentHelper.isEqual(currentDate, lastMoneyExchange.getDate())) {
						rate = lastMoneyExchange.getCurrencyExchange();
						accumulatedAmountInvoices += rate * i.totalAmount();
					} else {
						rate = this.computeMoneyExchange(i.getQuantity().getCurrency(), currencyTarget);
						accumulatedAmountInvoices += rate * i.totalAmount();
					}
				}
			temp = new BigDecimal(Double.toString(accumulatedAmountInvoices)).setScale(2, RoundingMode.HALF_UP);
			accumulatedAmountInvoices = temp.doubleValue();

			return accumulatedAmountInvoices;
		}
	}

	private double computeMoneyExchange(final String currencyBase, final String currencyTarget) {
		assert !StringHelper.isBlank(currencyBase);
		assert !StringHelper.isBlank(currencyTarget);

		MoneyExchange response;
		RestTemplate api;
		HttpHeaders headers;
		HttpEntity<String> parameters;
		ResponseEntity<ExchangeRate> objectResponse;
		ExchangeRate record;
		Double currencyExchange;
		Date moment;
		BigDecimal temp;

		try {

			api = new RestTemplate();

			headers = new HttpHeaders();
			headers.add("apikey", "cur_live_y7beSkQRf0YzYa7tbAozTeZOXZZEr7iYzdAQd8Eq");

			parameters = new HttpEntity<String>("parameters", headers);

			objectResponse = api.exchange( //				
				"https://api.currencyapi.com/v3/latest?base_currency={1}&currencies={2}", //
				HttpMethod.GET, //
				parameters, //
				ExchangeRate.class, //
				currencyBase,//
				currencyTarget);

			assert objectResponse != null && objectResponse.getBody() != null;
			record = objectResponse.getBody();

			assert record != null;
			currencyExchange = Double.valueOf(record.getData().get(currencyTarget).get("value"));
			temp = new BigDecimal(Double.toString(currencyExchange)).setScale(2, RoundingMode.HALF_UP);
			currencyExchange = temp.doubleValue();

			moment = MomentHelper.getCurrentMoment();

			response = new MoneyExchange();
			response.setCurrencyOriginal(currencyBase);
			response.setCurrencyTarget(currencyTarget);
			response.setDate(moment);
			response.setCurrencyExchange(currencyExchange);

			this.cacheExchangeRate(response);

			MomentHelper.sleep(1000); // HINT: need to pause the requests to the API a bit down to prevent DOS attacks

		} catch (final Throwable oops) {
			currencyExchange = null;
		}

		return currencyExchange;
	}

	private void cacheExchangeRate(final MoneyExchange object) {
		assert object != null;

		MoneyExchange lastExchange = this.repository.findLastMoneyExchange(object.getCurrencyOriginal());
		if (lastExchange != null)
			this.repository.deleteById(lastExchange.getId());

		this.repository.save(object);
	}
}
