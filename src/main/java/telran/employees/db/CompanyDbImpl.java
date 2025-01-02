package telran.employees.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import telran.employees.*;

public class CompanyDbImpl implements Company{

    private CompanyRepository repository;

    public CompanyDbImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterator<Employee> iterator() {
        return repository.getEmployees().iterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addEmployee'");
    }

    @Override
    public Employee getEmployee(long id) {
        return repository.getEmployees()
                        .stream()
                        .filter(e -> e.getId() == id)
                        .findFirst()
                        .orElse(null);
    }

    @Override
    public Employee removeEmployee(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeEmployee'");
    }

    @Override
    public int getDepartmentBudget(String department) {
        return repository.getEmployees()
                        .stream()
                        .filter(e -> department.equals(e.getDepartment()))
                        .mapToInt(Employee::computeSalary)
                        .sum();
    }

    @Override
    public String[] getDepartments() {
        return repository.getEmployees()
                        .stream()
                        .map(Employee::getDepartment)
                        .distinct()
                        .toArray(String[]::new);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();
        Manager[] res = new Manager[0];

        for (Employee employee : repository.getEmployees()) {
            if (employee instanceof Manager manager) {
                managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
            }
        }

        if (!managersFactor.isEmpty()) {
            res = managersFactor.lastEntry().getValue().toArray(res);
        }

        return res;
    }

}
