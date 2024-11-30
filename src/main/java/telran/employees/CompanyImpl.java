package telran.employees;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
    private static ReentrantReadWriteLock rw_lock = new ReentrantReadWriteLock();
    private static Lock readLock = rw_lock.readLock();
    private static Lock writeLock = rw_lock.writeLock();
    private static final ConcurrentHashMap<String, Object> filesLock = new ConcurrentHashMap<>();

    private class CompanyImplIterator implements Iterator<Employee> {
        private Iterator<Map.Entry<Long, Employee>> iterator;
        private Map.Entry<Long, Employee> currentEntry = null;

        public CompanyImplIterator() {
            this.iterator = employees.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            readLock.lock();
            try {
                return iterator.hasNext();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Employee next() {
            readLock.lock();
            try {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                currentEntry = iterator.next();
                return currentEntry.getValue();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void remove() {
            writeLock.lock();
            try {
                if (currentEntry == null) {
                    throw new IllegalStateException();
                }
                iterator.remove();
                removeEmployeeInOtherCollection(currentEntry.getValue());
                currentEntry = null;
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    public Iterator<Employee> iterator() {
        readLock.lock();
        try {
            return new CompanyImplIterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void addEmployee(Employee empl) {
        writeLock.lock();
        try {
            if (employees.putIfAbsent(empl.getId(), empl) == null) {
                employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
                if (empl instanceof Manager) {
                    Manager manager = (Manager) empl;
                    managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
                }
            } else {
                throw new IllegalStateException();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Employee getEmployee(long id) {
        readLock.lock();
        try {
            return employees.get(id);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Employee removeEmployee(long id) {
        writeLock.lock();
        try {
            Employee empl = employees.remove(id);
            removeEmployeeInOtherCollection(empl);
            return empl;
        } finally {
            writeLock.unlock();
        }
    }

    private void removeEmployeeInOtherCollection(Employee empl) {
        if (empl != null) {
            removeFromEmployeesDepartment(empl);
            if (empl instanceof Manager) {
                removeFromManagersFactor((Manager) empl);
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
        int res = 0;
        readLock.lock();
        try {
            List<Employee> list = employeesDepartment.get(department);
            if (list != null) {
                res = list.stream().mapToInt(Employee::computeSalary).sum();
            }
        } finally {
            readLock.unlock();
        }
        return res;
    }

    @Override
    public String[] getDepartments() {
        readLock.lock();
        try {
            return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Manager[] res = new Manager[0];
        readLock.lock();
        try {
            if (!managersFactor.isEmpty()) {
                res = managersFactor.lastEntry().getValue().toArray(Manager[]::new);
            }
        } finally {
            readLock.unlock();
        }
        return res;
    }

    @Override
    public void saveToFile(String fileName) {
        Object fileLock = filesLock.computeIfAbsent(fileName, k -> new Object());
        synchronized (fileLock) {
            try (PrintWriter printWriter = new PrintWriter(fileName)) {
                employees.values().stream().forEach(printWriter::println);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                filesLock.remove(fileName);
            }
        }
    }

    @Override
    public void restoreFromFile(String fileName) {
        Object fileLock = filesLock.computeIfAbsent(fileName, k -> new Object());
        synchronized (fileLock) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
                bufferedReader.lines().map(Employee::getEmployeeFromJSON).forEach(this::addEmployee);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                filesLock.remove(fileName);
            }
        }
    }

}
