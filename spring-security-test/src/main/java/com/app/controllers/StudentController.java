package com.app.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private List<Student> studentList = Arrays.asList(
			new Student(1, "student1"),
			new Student(2, "student2"),
			new Student(3, "student3")
	);
	
	@GetMapping("/{id}")
	public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
		logger.info("Inside the get student by id!");
		Student student = studentList.stream().filter(studentInfo->studentInfo.id == id).findFirst().get();
		logger.info("student :"+student);
		return new ResponseEntity<>(student, HttpStatus.FOUND);
	}
	
	private static class Student{
		private String name;
		private Integer id;
		
		private Student(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Integer getId() {
			return id;
		}

		@Override
		public String toString() {
			return "Student [name=" + name + ", id=" + id + "]";
		}
	}
}
