package telran.employees;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import telran.io.Persistable;

public class CompanyImpl implements Company, Persistable {
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    private class CompanyImplIterator implements Iterator<Employee> {
        private Iterator<Map.Entry<Long, Employee>> iterator;
        private Map.Entry<Long, Employee> currentEntry = null;

        public CompanyImplIterator() {
            this.iterator = employees.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Employee next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            currentEntry = iterator.next();
            return currentEntry.getValue();
        }

        @Override
        public void remove() {
            if (currentEntry == null) {
                throw new IllegalStateException();    
            }
            iterator.remove();
            removeEmployeeInOtherCollection(currentEntry.getValue());
            currentEntry = null;
        }
    }

    @Override
    public Iterator<Employee> iterator() {
        return new CompanyImplIterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        if (employees.putIfAbsent(empl.getId(), empl) == null) {
            employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
            if (empl instanceof Manager) {
                Manager manager = (Manager) empl;
                managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Employee getEmployee(long id) {
        return employees.get(id);
    }

    @Override
    public Employee removeEmployee(long id) {
        Employee empl = employees.remove(id);
        removeEmployeeInOtherCollection(empl);
        return empl;
    }

    private void removeEmployeeInOtherCollection(Employee empl) {
        if (empl != null) {
            removeFromEmployeesDepartment(empl);
            if (empl instanceof Manager) {
                removeFromManagersFactor((Manager)empl);
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    private void removeFromManagersFactor(Manager empl) {
        float factor = empl.getFactor();
        List<Manager> list = managersFactor.get(factor);
        if (list != null) {
            list.remove(empl);
            if (list.isEmpty()) {
                managersFactor.remove(factor);
            }
        }
    }

    private void removeFromEmployeesDepartment(Employee empl) {
        String department = empl.getDepartment();
        List<Employee> list = employeesDepartment.get(department);
        if (list != null) {
            list.remove(empl);
            if (list.isEmpty()) {
                employeesDepartment.remove(department);
            }
        }  
    }

    @Override
    public int getDepartmentBudget(String department) {
        List<Employee> list = employeesDepartment.get(department);
        int res = 0;
        if (list != null) {
            res = list.stream().mapToInt(Employee::computeSalary).sum();    
        }
         return res;   
    }

    @Override
    public String[] getDepartments() {
        return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Manager[] res = new Manager[0];
        if (!managersFactor.isEmpty()) {
            res = managersFactor.lastEntry().getValue().toArray(Manager[]::new);
        }
        return res;
    }

    @Override
    public void saveToFile(String fileName) {
        try {
            PrintWriter printWriter = new PrintWriter(fileName);
            employees.values().stream().forEach(e -> printWriter.println(e.toString()));
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreFromFile(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            bufferedReader.lines().map(Employee::getEmployeeFromJSON).forEach(this::addEmployee);
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
