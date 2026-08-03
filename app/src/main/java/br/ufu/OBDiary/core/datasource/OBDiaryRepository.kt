package br.ufu.OBDiary.core.datasource

class OBDiaryRepository(
    private val vehicleDao: VehicleDao,
    private val refuelingDao: RefuelingDao,
    private val repairDao: RepairDao
) {
    val allVehicles = vehicleDao.getAllVehiclesWithRefuelsAndRepairs()

    suspend fun insertVehicle(vehicle: VehicleEntity) {
        vehicleDao.insert(vehicle)
    }

    suspend fun insertRefuel(refuel: RefuelingEntity) {
        refuelingDao.insert(refuel)
    }

    suspend fun insertRepair(repair: RepairEntity) {
        repairDao.insert(repair)
    }

    suspend fun updateVehicle(vehicle: VehicleEntity) {
        vehicleDao.update(vehicle)
    }

    suspend fun updateRefuel(refuel: RefuelingEntity) {
        refuelingDao.update(refuel)
    }

    suspend fun updateRepair(repair: RepairEntity) {
        repairDao.update(repair)
    }

    suspend fun deleteVehicleById(id: Int) {
        vehicleDao.deleteById(id)
    }

    suspend fun deleteRefuelById(id: Int) {
        refuelingDao.deleteById(id)
    }

    suspend fun deleteRepairById(id: Int) {
        repairDao.deleteById(id)
    }
}