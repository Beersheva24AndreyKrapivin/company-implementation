package telran.employees.db.jpa;

import org.json.JSONObject;

import jakarta.persistence.Entity;
import telran.employees.Employee;
import telran.employees.SalesPerson;

@Entity
public class SalesPersonEntity extends WageEmployeeEntity{
    //TODO
    private float percent;
    private long sales;

    @Override
    protected void fromEmployeeDto(Employee empl) {
        //TODO
        //filling relevant fields
        if (empl != null) {
            super.fromEmployeeDto(empl);
            SalesPerson salesPerson = (SalesPerson) empl;
            this.percent = salesPerson.getPercent();
            this.sales = salesPerson.getSales();
        }

    }

    @Override
    protected void toJsonObject(JSONObject jsonObj) {
        //TODO
        //put appropriate filds to JSONObject
        super.toJsonObject(jsonObj);
        jsonObj.put("percent", percent);
        jsonObj.put("sales", sales);
    }
}
