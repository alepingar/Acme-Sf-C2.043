
package acme.features.client.dashboard;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contract.Contract;
import acme.entities.contract.ProgressLog;
import acme.forms.ClientDashboard;
import acme.roles.client.Client;
import acme.systemConfiguration.SystemConfiguration;

@Service
public class ClientDashboardShowService extends AbstractService<Client, ClientDashboard> {

	@Autowired
	private ClientDashboardRepository repository;


	@Override
	public void authorise() {

		final boolean status = super.getRequest().getPrincipal().hasRole(Client.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int clientId = super.getRequest().getPrincipal().getActiveRoleId();

		ClientDashboard clientDashboard;

		clientDashboard = new ClientDashboard();

		Collection<ProgressLog> progressLogsPublished = this.repository.findAllProgressLogs().stream().filter(x -> !x.getDraftMode()).toList();
		Collection<Contract> myPublishedContracts = this.repository.findContractsByClientId(clientId).stream().filter(x -> !x.getDraftMode()).toList();
		Collection<Integer> myContractsIds = myPublishedContracts.stream().map(x -> x.getId()).toList();
		Collection<Money> myBudgets = this.repository.findBudgetsByClientId(clientId);
		Map<String, List<Money>> budgetsByCurrency = myBudgets.stream().collect(Collectors.groupingBy(Money::getCurrency));

		Map<String, Double> averageBudgetPerCurrency = budgetsByCurrency.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.calcularMedia(entry.getValue()).getAmount()));

		Map<String, Double> maximumBudgetPerCurrency = budgetsByCurrency.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.calcularMaximo(entry.getValue()).getAmount()));

		Map<String, Double> minimumBudgetPerCurrency = budgetsByCurrency.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.calcularMinimo(entry.getValue()).getAmount()));

		Map<String, Double> deviationBudgetPerCurrency = budgetsByCurrency.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.calcularDesviacion(entry.getValue()).getAmount()));

		List<SystemConfiguration> systemConfiguration = this.repository.findSystemConfiguration();

		String[] supportedCurrencies = systemConfiguration.get(0).aceptedCurrencies.split(",");
		double progressLogsBelow25 = progressLogsPublished.stream().filter(x -> myContractsIds.contains(x.getContract().getId())).filter(x -> x.getCompletenessPercentage() < 25.0).count();
		double progressLogsBetween25And50 = progressLogsPublished.stream().filter(x -> myContractsIds.contains(x.getContract().getId())).filter(x -> x.getCompletenessPercentage() >= 25.0 && x.getCompletenessPercentage() <= 50.0).count();
		double progressLogsBetween50And75 = progressLogsPublished.stream().filter(x -> myContractsIds.contains(x.getContract().getId())).filter(x -> x.getCompletenessPercentage() > 50.0 && x.getCompletenessPercentage() <= 75.0).count();
		double progressLogsAbove75 = progressLogsPublished.stream().filter(x -> myContractsIds.contains(x.getContract().getId())).filter(x -> x.getCompletenessPercentage() > 75.0).count();

		clientDashboard.setProgressLogsBelow25(progressLogsBelow25);
		clientDashboard.setProgressLogsBetween25And50(progressLogsBetween25And50);
		clientDashboard.setProgressLogsBetween50And75(progressLogsBetween50And75);
		clientDashboard.setProgressLogsAbove75(progressLogsAbove75);

		clientDashboard.setMaximumBudgetPerCurrency(maximumBudgetPerCurrency);
		clientDashboard.setMinimumBudgetPerCurrency(minimumBudgetPerCurrency);
		clientDashboard.setAverageBudgetPerCurrency(averageBudgetPerCurrency);
		clientDashboard.setDeviationBudgetPerCurrency(deviationBudgetPerCurrency);

		clientDashboard.setSupportedCurrencies(supportedCurrencies);
		super.getBuffer().addData(clientDashboard);

	}

	@Override
	public void unbind(final ClientDashboard object) {

		Dataset dataset;

		dataset = super.unbind(object, "progressLogsBelow25", "progressLogsBetween25And50", //
			"progressLogsBetween50And75", "progressLogsAbove75", //
			"averageBudgetPerCurrency", "deviationBudgetPerCurrency", "minimumBudgetPerCurrency", "maximumBudgetPerCurrency", "supportedCurrencies");

		super.getResponse().addData(dataset);

	}

	private Money calcularMedia(final Collection<Money> budgets) {
		Money moneyFinal = new Money();
		moneyFinal.setCurrency("USD");
		moneyFinal.setAmount(budgets.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).average().orElse(Double.NaN));

		return moneyFinal;
	}

	private Money calcularMaximo(final Collection<Money> budgets) {
		Money moneyFinal = new Money();
		moneyFinal.setCurrency("USD");
		moneyFinal.setAmount(budgets.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).max().orElse(Double.NaN));
		return moneyFinal;
	}

	private Money calcularMinimo(final Collection<Money> budgets) {
		Money moneyFinal = new Money();
		moneyFinal.setCurrency("USD");
		moneyFinal.setAmount(budgets.stream().map(x -> x.getAmount()).mapToDouble(Double::doubleValue).min().orElse(Double.NaN));
		return moneyFinal;

	}

	private Money calcularDesviacion(final Collection<Money> budgets) {
		Money desviacion = new Money();
		desviacion.setCurrency("USD");

		double media = budgets.stream().mapToDouble(Money::getAmount).average().orElse(Double.NaN);

		double sumaDiferenciasCuadradas = budgets.stream().mapToDouble(budget -> Math.pow(budget.getAmount() - media, 2)).sum();

		double varianza = sumaDiferenciasCuadradas / budgets.size();

		double desviacionEstandar = Math.sqrt(varianza);

		desviacion.setAmount(desviacionEstandar);

		return desviacion;
	}
}
