
package acme.entities.banner;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Banner extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date				instationUpdateMoment;

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date				startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date				finishTime;

	@URL
	private String				linkPicture;

	@NotBlank
	@Length(max = 76)
	private String				slogan;

	@URL
	private String				linkDocument;

	// Derived attributes -----------------------------------------------------

}
