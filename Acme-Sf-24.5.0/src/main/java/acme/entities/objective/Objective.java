
package acme.entities.objective;

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
public class Objective extends AbstractEntity {

	/*
	 * An objective allows an authenticated principal to define a goal or end
	 * towards which the actions or operations of a specific project are directed.
	 * The system must store the following data about them: an instantiation moment (in the past),
	 * a title (not blank, shorter than 76 characters), a description (not blank, shorter than 101 characters),
	 * a priority (“Low”, “Medium”, “High”), a status to indicate whether it is critical or not,
	 * a duration (must start at any moment after the instantiation moment),
	 * and an optional link with further information.
	 */

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Past
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				instantiationMoment;

	@NotBlank
	@Length(max = 75)
	private String				title;

	@NotBlank
	@Length(max = 100)
	private String				description;

	@NotNull
	private ObjPriority			priority;

	@NotNull
	private Boolean				criticalStatus;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				startDuration;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				endDuration;

	@URL
	private String				infLink;

}
