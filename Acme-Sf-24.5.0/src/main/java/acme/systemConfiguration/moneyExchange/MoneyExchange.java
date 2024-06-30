
package acme.systemConfiguration.moneyExchange;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MoneyExchange extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Query attributes -------------------------------------------------------

	@NotBlank
	@Pattern(regexp = "[A-Z]{3}")
	private String				currencyOriginal;

	@NotBlank
	@Pattern(regexp = "[A-Z]{3}")
	private String				currencyTarget;

	// Response attributes ----------------------------------------------------

	private double				currencyExchange;

	@NotNull
	private Date				date;
}
