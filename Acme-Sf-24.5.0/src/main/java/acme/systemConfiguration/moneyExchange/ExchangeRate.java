
package acme.systemConfiguration.moneyExchange;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRate {

	public Map<String, String>				meta;

	public Map<String, Map<String, String>>	data;
}
