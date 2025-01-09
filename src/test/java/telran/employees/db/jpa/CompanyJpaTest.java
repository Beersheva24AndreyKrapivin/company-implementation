package telran.employees.db.jpa;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;

import telran.employees.CompanyTest;
import telran.employees.db.CompanyDbImpl;
import telran.employees.db.CompanyRepository;
import telran.employees.db.jpa.config.EmployeesPersistanceUnitInfo;

public class CompanyJpaTest extends CompanyTest {
    
    @BeforeEach    
    @Override
    protected void setCompany() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create");
        CompanyRepository repository = new CompanyRepositoryJpaImpl(new EmployeesPersistanceUnitInfo(), properties);
        company = new CompanyDbImpl(repository);
        super.setCompany();
    }
}
