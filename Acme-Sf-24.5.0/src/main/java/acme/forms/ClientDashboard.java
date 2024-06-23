
package acme.forms;

import java.util.Map;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDashboard extends AbstractForm {

	/*
	 * The system must handle client dashboards with the following data:
	 * total number of progress logs with a completeness rate below 25%,
	 * between 25% and 50%, between 50% and 75%, and above 75%; average,
	 * deviation, minimum, and maximum budget of the contracts.
	 */

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						progressLogsBelow25;
	Double						progressLogsBetween25And50;
	Double						progressLogsBetween50And75;
	Double						progressLogsAbove75;

	Map<String, Double>			averageBudgetPerCurrency;
	Map<String, Double>			deviationBudgetPerCurrency;
	Map<String, Double>			minimumBudgetPerCurrency;
	Map<String, Double>			maximumBudgetPerCurrency;

	String[]					supportedCurrencies;
}
