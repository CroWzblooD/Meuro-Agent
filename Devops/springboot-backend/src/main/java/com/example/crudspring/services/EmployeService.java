package com.example.crudspring.services;

import com.example.crudspring.models.Employe;
import com.example.crudspring.repository.EmployeRepository;
import com.example.crudspring.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeService {
    
    @Autowired
    private EmployeRepository employeRepository;
    
    public List<Employe> getAllEmployees() {
        return employeRepository.findAll();
    }
    
    public Employe createEmployee(Employe employe) {
        return employeRepository.save(employe);
    }
    
    public Employe getEmployeeById(Long id) {
        Optional<Employe> employe = employeRepository.findById(id);
        if (employe.isPresent()) {
            return employe.get();
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
    }
    
    public Employe updateEmployee(Long id, Employe employeDetails) {
        Employe employe = getEmployeeById(id);
        employe.setFirstName(employeDetails.getFirstName());
        employe.setLastName(employeDetails.getLastName());
        employe.setEmail(employeDetails.getEmail());
        return employeRepository.save(employe);
    }
    
    public void deleteEmployee(Long id) {
        Employe employe = getEmployeeById(id);
        employeRepository.delete(employe);
    }
}