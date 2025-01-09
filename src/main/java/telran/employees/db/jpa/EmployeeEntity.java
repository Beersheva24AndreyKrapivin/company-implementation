package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.*;
import telran.employees.*;

@Table(name = "employees")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("Employee")
public class EmployeeEntity {

    @Id
    private long id;
    @Column(name = "basic_salary")
    private int basicSalary;
    private String department;
    
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevant fields, example: id = empl.getId();...
        if (empl != null) {
            this.id = empl.getId();
            this.basicSalary = empl.computeSalary();
            this.department = empl.getDepartment();
        }
    }

    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject, example: jsonObj.put("id", id)
        jsonObj.put("id", id);
        jsonObj.put("basicSalary", basicSalary);
        jsonObj.put("department", department == null ? "" : department);
    }
}
