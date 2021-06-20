package com.app.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.Lists;

@RestController
@RequestMapping(path="management/api/v1/students")
public class StudentManagementController 
{
	
	private List<Student> studentList = Arrays.asList(
			new Student(1, "student1"),
			new Student(2, "student2"),
			new Student(3, "student3")
	);
	
	@GetMapping("/list")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMINTRAINEE')")
	public ResponseEntity<List<Student>> listStudents(){
		return new ResponseEntity<>(studentList, HttpStatus.OK);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('student:write')")
	public ResponseEntity<Void> addNewStudent() {
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PatchMapping
	@PreAuthorize("hasAuthority('student:write')")
	public ResponseEntity<String> updateSudent(){
		return new ResponseEntity<>("student info update is success!", HttpStatus.OK);
	}
	
	@DeleteMapping
	@PreAuthorize("hasAuthority('student:write')")
	public ResponseEntity<String> deleteStudent(){
		return new ResponseEntity<>("student info delete is success!", HttpStatus.OK);
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