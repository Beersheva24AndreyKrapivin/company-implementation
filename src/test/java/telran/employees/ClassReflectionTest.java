package telran.employees;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import telran.employees.db.jpa.EmployeeEntity;
import telran.employees.db.jpa.EmployeesMapper;

class ClassReflectionTest {

    @Test
    void testEmployeeConversion() {
        assertEmpoyee(new Employee());
        assertEmpoyee(new Manager());
        assertEmpoyee(new WageEmployee());
        assertEmpoyee(new SalesPerson());
    }

    void assertEmpoyee(Employee employeeOrigin) {
        EmployeeEntity employeeEntity = EmployeesMapper.toEmployeeEntityFromDto(employeeOrigin);
        assertEquals("telran.employees.db.jpa." + employeeOrigin.getClass().getSimpleName() + "Entity", employeeEntity.getClass().getName());
        
        Employee employeeRes = EmployeesMapper.toEmployeeDtoFromEntity(employeeEntity);
        assertEquals("telran.employees." + employeeOrigin.getClass().getSimpleName(), employeeRes.getClass().getName());
    }

}
