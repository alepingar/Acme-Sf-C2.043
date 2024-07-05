
package acme.entities.training;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.entities.projects.Project;
import acme.roles.Developer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrainingModule extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Column(unique = true)
	@NotBlank
	@Pattern(regexp = "[A-Z]{1,3}-[0-9]{3}", message = "The code must follow the pattern XXX-XXX.")
	private String				code;

	@Past
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date				creationMoment;

	@NotBlank
	@Length(max = 100)
	private String				details;

	@NotNull
	private Difficulty			difficultyLevel;

	@Past
	@Temporal(TemporalType.TIMESTAMP)
	//que sea despues de la creacion (en servicio)
	private Date				updatedMoment;

	@URL
	private String				optionalLink;

	@NotNull
	@Min(0)
	private Double				totalTime;

	@NotNull
	private Boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@ManyToOne
	@NotNull
	@Valid
	private Project				project;

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	private Developer			developer;

	// Derived attributes -----------------------------------------------------

	/*
	 * @AssertTrue(message = "El momento de actualizacion del modulo debe ser posterior a su momento de creacion")
	 * public boolean isUpdateMomentAfterCreationMoment() {
	 * return this.updatedMoment != null && this.creationMoment != null && this.updatedMoment.after(this.creationMoment);
	 * }
	 * 
	 * @Transient
	 * public Double totalTime(final Collection<TrainingSession> sesiones) {
	 * double estimatedTime = 0;
	 * if (!sesiones.isEmpty())
	 * for (final TrainingSession session : sesiones) {
	 * final long diffMs = session.getEndDate().getTime() - session.getStartDate().getTime();
	 * final double diffH = diffMs / (1000.0 * 60 * 60);
	 * estimatedTime = estimatedTime + diffH;
	 * }
	 * return estimatedTime;
	 * }
	 * 
	 */

}
