package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.Employee;
import telran.employees.Manager;

@Entity
public class ManagerEntity extends EmployeeEntity{
    private float factor;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevant fields
        if (empl != null) {
            super.fromEmployeeDto(empl);
            Manager manager = (Manager) empl;
            this.factor = manager.getFactor();
        }

    }

    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject
        super.toJsonObject(jsonObj);
        jsonObj.put("factor", factor);
    }
}
