package com.sisregistration.bmwsis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Firstcontroller {
    @GetMapping("/sis")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/student")
    public String student() {
        return "redirect:/student/login"; // Redirect to new student login
    }

    @GetMapping("/faculty")
    public String faculty() {
        return "redirect:/faculty/login"; // Redirect to faculty login
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/login"; // Redirect to admin login
    }
}