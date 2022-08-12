package com.sp.poc.rsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.poc.rsm.dtos.EmployeeDTO;
import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.enums.State;
import com.sp.poc.rsm.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EmployeeController.class})
class EmployeeControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void addEmployee() throws Exception {

        Employee employee = new Employee(1L, "Test Root", "APAC", State.ADDED, 29);
        EmployeeDTO employeeDto = new EmployeeDTO(null, "John Root", "APAC", State.ADDED, 29);
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(employee);
        mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void getEmployee() throws Exception {
        long id = 1L;
        Employee employee = new Employee(1L, "Test Root", "APAC", State.ADDED, 29);
        when(employeeService.getEmployee(id)).thenReturn(employee);
        mockMvc.perform(get("/api/v1/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andDo(print());
    }

    @Test
    void updateEmployee() throws Exception {
        long id = 1L;
        Employee employee = new Employee(1L, "John Root", "EPAC", State.ADDED, 29);
        Employee employeeUpdated = new Employee(1L, "Test Root", "APAC", State.ADDED, 29);
        when(employeeService.getEmployee(id)).thenReturn(employee);
        when(employeeService.updateEmployee(any(Employee.class))).thenReturn(employeeUpdated);
        mockMvc.perform(put("/api/v1/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldFailUpdateEmployee() throws Exception {
        long id = 1L;
        Employee employee = new Employee(1L, "John Root", "EPAC", State.APPROVED, 29);
        Employee existingEmployee = new Employee(1L, "Test Root", "APAC", State.ADDED, 29);
        when(employeeService.getEmployee(id)).thenReturn(existingEmployee);
        when(employeeService.updateEmployee(any(Employee.class))).thenReturn(employee);
        mockMvc.perform(put("/api/v1/employees").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateEmployeeWithIdAndEvent() throws Exception {
        long id = 1L;
        Employee employee = new Employee(1L, "John Root", "EPAC", State.ADDED, 29);
        Employee employeeUpdated = new Employee(1L, "John Root", "APAC", State.IN_CHECK, 29);
        when(employeeService.getEmployee(id)).thenReturn(employee);
        when(employeeService.updateEmployeeState(any(Long.class), any(Event.class))).thenReturn(employeeUpdated);
        mockMvc.perform(put("/api/v1/employees/{id}/{event}", id, Event.BEGIN_CHECK).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.state").value(State.IN_CHECK.name()))
                .andDo(print());
    }
}
