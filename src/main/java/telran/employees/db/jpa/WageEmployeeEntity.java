package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.Entity;
import telran.employees.Employee;
import telran.employees.WageEmployee;

@Entity
public class WageEmployeeEntity extends EmployeeEntity{
    //TODO
    private int wage;
    private int hours;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevant fields
        if (empl != null) {
            super.fromEmployeeDto(empl);
            WageEmployee wageEmployee = (WageEmployee) empl;
            this.wage = wageEmployee.getWage();
            this.hours = wageEmployee.getHours();
        }

    }

    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject
        super.toJsonObject(jsonObj);
        jsonObj.put("wage", wage);
        jsonObj.put("hours", hours);
    }
}
