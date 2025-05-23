
package acme.features.technician.maintenanceRecordTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenanceRecordTask.MaintenanceRecordTask;
import acme.realms.Technician;

@GuiController
public class TechnicianMaintenanceRecordTaskController extends AbstractGuiController<Technician, MaintenanceRecordTask> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskCreateService createService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);

	}
}
