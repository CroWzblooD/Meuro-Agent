package com.example.crudspring.controllers;

import com.example.crudspring.models.Employe;
import com.example.crudspring.services.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeController {
    
    @Autowired
    private EmployeService employeService;
    
    @GetMapping
    public List<Employe> getAllEmployees() {
        return employeService.getAllEmployees();
    }
    
    @PostMapping
    public Employe createEmployee(@RequestBody Employe employe) {
        return employeService.createEmployee(employe);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Employe> getEmployeeById(@PathVariable Long id) {
        Employe employe = employeService.getEmployeeById(id);
        return ResponseEntity.ok(employe);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employe> updateEmployee(@PathVariable Long id, @RequestBody Employe employeDetails) {
        Employe updatedEmploye = employeService.updateEmployee(id, employeDetails);
        return ResponseEntity.ok(updatedEmploye);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}