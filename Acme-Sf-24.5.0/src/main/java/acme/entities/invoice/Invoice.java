
package acme.entities.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.client.data.datatypes.Money;
import acme.entities.sponsorship.Sponsorship;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Invoice extends AbstractEntity {

	//Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//------------------------------------------------------------------------
	@NotBlank
	@Pattern(regexp = "^IN-[0-9]{4}-[0-9]{4}$")
	@Column(unique = true)
	private String				code;

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dueDate;

	@NotNull
	@Valid
	private Money				quantity;

	@Min(0)
	private double				tax;

	@URL
	private String				link;

	private boolean				isPublished;

	// Derived attributes -----------------------------------------------------


	@Transient
	public double totalAmount() {
		BigDecimal temp;
		double total;

		total = this.quantity.getAmount() + this.quantity.getAmount() * (this.tax / 100);
		temp = new BigDecimal(Double.toString(total)).setScale(2, RoundingMode.HALF_UP);
		total = temp.doubleValue();

		return total;
	}

	// Relationships ----------------------------------------------------------


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	private Sponsorship sponsorship;
}
