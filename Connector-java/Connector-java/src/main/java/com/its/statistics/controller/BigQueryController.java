package com.its.statistics.controller;

import com.its.statistics.dto.StudentRequest;
import com.its.statistics.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bq")
public class BigQueryController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testBQ() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<String> values = studentService.runQuerySQL("SELECT CURRENT_DATE() as current_date");
            resp.put("ok", true);
            resp.put("value", values);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<?>> getStudentsFromBQ(@RequestParam(required = false, defaultValue = "100") int limit) {
        // Delego a StudentService che gestisce fallback a DB locale
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping("/students")
    public ResponseEntity<Map<String,Object>> postStudentToBQ(@RequestBody StudentRequest req) {
        Map<String, Object> resp = new HashMap<>();
        try {
            boolean inserted = studentService.insertStudent(req, null, null);
            if (inserted) {
                resp.put("ok", true);
                return ResponseEntity.status(HttpStatus.CREATED).body(resp);
            } else {
                resp.put("ok", false);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            }
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @PostMapping("/local/students")
    public ResponseEntity<Map<String,Object>> postLocalStudent(@RequestBody StudentRequest req) {
        Map<String, Object> resp = new HashMap<>();
        try {
            studentService.createStudent(req);
            resp.put("ok", true);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }
}
