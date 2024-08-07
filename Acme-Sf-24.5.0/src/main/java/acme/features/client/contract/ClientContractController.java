
package acme.features.client.contract;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.contract.Contract;
import acme.roles.client.Client;

@Controller
public class ClientContractController extends AbstractController<Client, Contract> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientContractListService		listService;

	@Autowired
	private ClientContractCreateService		createService;

	@Autowired
	private ClientContractShowService		showService;

	@Autowired
	private ClientContractDeleteService		deleteService;

	@Autowired
	private ClientContractUpdateService		updateService;

	@Autowired
	private ClientContractPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	private void initialise() {

		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
