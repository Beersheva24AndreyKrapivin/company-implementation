package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.Employee;
import telran.employees.Manager;

@Entity
@DiscriminatorValue("Manager")
public class ManagerEntity extends EmployeeEntity{
    private float factor;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevant fields
        if (empl != null) {
            super.fromEmployeeDto(empl);
            this.factor = ((Manager) empl).getFactor();
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
