
package acme.features.technician.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.maintenanceRecord.MaintenanceStatus;
import acme.forms.TechnicianDashboard;
import acme.realms.Technician;

@Service
@GuiService
public class TechnicianDashboardShowService extends AbstractGuiService<Technician, TechnicianDashboard> {

	//Internal state ----------------------------------------------------------

	@Autowired
	private TechnicianDashboardRepository repository;

	//AbstractGuiService state ----------------------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;
		Principal principal = super.getRequest().getPrincipal();
		int userAccountId = principal.getAccountId();

		// Buscar al técnico que está actualmente logueado
		Technician technician = this.repository.findOneTechnicianByUserAccoundId(userAccountId);

		if (technician != null)
			// Verificar si el técnico está registrado en el sistema
			authorised = true;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		final TechnicianDashboard dashboard = new TechnicianDashboard();

		Principal principal;
		int userAccountId;
		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		final Technician technician = this.repository.findOneTechnicianByUserAccoundId(userAccountId);

		//NumOfMaintenanceRecordsByStatus
		final Map<String, Integer> numOfRecordsByStatus = new HashMap<>();
		final Integer pendingRecords = this.repository.countMaintenanceRecordsByStatus(technician.getId(), MaintenanceStatus.PENDING).orElse(0);
		final Integer inProgressRecords = this.repository.countMaintenanceRecordsByStatus(technician.getId(), MaintenanceStatus.IN_PROGRESS).orElse(0);
		final Integer completedRecords = this.repository.countMaintenanceRecordsByStatus(technician.getId(), MaintenanceStatus.COMPLETED).orElse(0);

		numOfRecordsByStatus.put("PENDING", pendingRecords);
		numOfRecordsByStatus.put("IN_PROGRESS", inProgressRecords);
		numOfRecordsByStatus.put("COMPLETED", completedRecords);

		// Pasar el mapa al dashboard
		dashboard.setNumberOfRecordsGroupedByStatus(numOfRecordsByStatus);

		super.getBuffer().addData(dashboard);

		// Maintenance record with the nearest inspection due date
		MaintenanceRecord nearestRecord = this.repository.findNearestInspectionRecordsByTechnicianId(technician.getId()).stream().findFirst().orElse(null);
		Date nearestRecordDate = nearestRecord.getNextInspectionDue();
		dashboard.setNearestInspectionMaintenanceRecord(nearestRecordDate);

		// Obtener todos los Aircraft asociados a MaintenanceRecords del Technician
		List<Aircraft> allAircrafts = this.repository.findTopFiveAircraftsByTechnicianId(technician.getId());

		// Seleccionar solo los 5 primeros (los que tienen más tareas)
		List<Aircraft> topFiveAircrafts = allAircrafts.stream().limit(5).toList();

		dashboard.setTopFiveAircrafts(topFiveAircrafts);

		super.getBuffer().addData(dashboard);

		// Statistics on the estimated cost of maintenance records in the last year
		dashboard.setAverageEstimatedCost(this.repository.findAverageEstimatedCost(technician.getId()));
		dashboard.setDeviationEstimatedCost(this.repository.findDeviationEstimatedCost(technician.getId()));
		dashboard.setMinEstimatedCost(this.repository.findMinEstimatedCost(technician.getId()));
		dashboard.setMaxEstimatedCost(this.repository.findMaxEstimatedCost(technician.getId()));

		// Statistics on the estimated duration of tasks in which the technician is involved
		dashboard.setAverageEstimatedDuration(this.repository.findAverageEstimatedDuration(technician.getId()));
		dashboard.setDeviationEstimatedDuration(this.repository.findDeviationEstimatedDuration(technician.getId()));
		dashboard.setMinEstimatedDuration(this.repository.findMinEstimatedDuration(technician.getId()));
		dashboard.setMaxEstimatedDuration(this.repository.findMaxEstimatedDuration(technician.getId()));

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final TechnicianDashboard object) {

		// Convertimos el Map de maintenanceStatus a un SelectChoices
		SelectChoices maintenanceStatusChoices = new SelectChoices();

		if (object.getNumberOfRecordsGroupedByStatus() != null)
			object.getNumberOfRecordsGroupedByStatus().forEach((status, count) -> {
				// Agregamos cada estado con su conteo como una opción al SelectChoices
				boolean isSelected = false; // Establece esta variable según la lógica que necesites
				maintenanceStatusChoices.add(status.toString(), count.toString(), isSelected);
			});

		// Convertimos la lista de Aircraft a un String separado por "/"
		String formattedAircrafts = object.getTopFiveAircrafts().stream().map(Aircraft::getId)  // Mantiene los IDs como Integer
			.map(String::valueOf)  // Convertimos cada Integer a String solo al momento de concatenar
			.reduce((a, b) -> a + " / " + b).orElse("No aircrafts found");

		// Crear el dataset para pasar los datos al JSP
		Dataset dataset = super.unbindObject(object, "nearestInspectionMaintenanceRecord", "averageEstimatedCost", "deviationEstimatedCost", "minEstimatedCost", "maxEstimatedCost", "averageEstimatedDuration", "deviationEstimatedDuration",
			"minEstimatedDuration", "maxEstimatedDuration");

		// Insertar las opciones convertidas en el dataset
		dataset.put("maintenanceStatus", maintenanceStatusChoices);

		// Añadir la lista formateada como un String al dataset
		dataset.put("topFiveAircrafts", formattedAircrafts);

		// Añadir el mapa de estados al dataset (Para gráfico Chart.js)
		dataset.put("numberOfRecordsGroupedByStatus", object.getNumberOfRecordsGroupedByStatus());

		// Añadir el dataset al response
		super.getResponse().addData(dataset);
	}
}
