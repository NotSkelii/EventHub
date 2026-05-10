package com.skeli.dashboardservice.controller;

import com.skeli.dashboardservice.dto.AnalyticsEventDto;
import com.skeli.dashboardservice.dto.AuthResponseDto;
import com.skeli.dashboardservice.dto.EventCreateDto;
import com.skeli.dashboardservice.dto.EventDto;
import com.skeli.dashboardservice.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        System.out.println("=== LOGIN PAGE REQUESTED ===");
        System.out.println("Template path: src/main/resources/templates/login.html");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }


    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login";
        }

        List<EventDto> events = dashboardService.getEvents(token);
        List<AnalyticsEventDto> analytics = dashboardService.getAnalytics(token);

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("events", events);
        model.addAttribute("analytics", analytics);
        model.addAttribute("eventCount", events.size());
        model.addAttribute("analyticsCount", analytics.size());
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/events/create")
    public String showCreateEventPage(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login";
        }
        model.addAttribute("event", new EventCreateDto());
        return "create-event";
    }

    @GetMapping("/check-session")
    @ResponseBody
    public String checkSession(HttpSession session) {
        String token = (String) session.getAttribute("token");
        return "Token: " + (token != null) + ", Username: " + session.getAttribute("username");
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        AuthResponseDto auth = dashboardService.login(username, password);

        if (auth != null && auth.getToken() != null) {
            session.setAttribute("token", auth.getToken());
            session.setAttribute("username", auth.getUsername());
            session.setAttribute("role", auth.getRole());
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid credentials");
        return "login";
    }

    @PostMapping("/register")
    public String registerPage(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        try {
            AuthResponseDto auth = dashboardService.register(username, email, password);
            if (auth != null && auth.getToken() != null) {
                session.setAttribute("token", auth.getToken());
                session.setAttribute("username", auth.getUsername());
                session.setAttribute("role", auth.getRole());
                return "redirect:/dashboard";
            }
            model.addAttribute("error", "Registration failed");
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed");
            return "register";
        }
    }

    @PostMapping("/events/create")
    public String createEvent(@ModelAttribute EventCreateDto eventDto,
                              HttpSession session,
                              Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login";
        }
        // Debug logging
        log.info("=== Creating Event ===");
        log.info("Name: {}", eventDto.getName());
        log.info("Venue: {}", eventDto.getVenue());
        log.info("City: {}", eventDto.getCity());
        log.info("Category: {}", eventDto.getCategory());
        log.info("Event Date: {}", eventDto.getEventDate());
        log.info("Total Seats: {}", eventDto.getTotalSeats());
        log.info("Price: {}", eventDto.getPrice());


        try {
            dashboardService.createEvent(eventDto, token);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create event: " + e.getMessage());
            return "create-event";
        }
    }

    @PostMapping("/events/reserve")
    public String reserveSeats(@RequestParam String eventId, @RequestParam int quantity, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login";
        }

        try {
            dashboardService.reserveSeats(eventId, quantity, token);
            redirectAttributes.addFlashAttribute("success", "Reserved " + quantity +
                    " seat(s) successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reserve seats: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/events/cancel-reservation")
    public String cancelSeats(@RequestParam String eventId, @RequestParam int quantity, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login";
        }

        try {
            dashboardService.cancelSeats(eventId, quantity, token);
            redirectAttributes.addFlashAttribute("success", "Cancelled " + quantity + " seat(s) successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel seats: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}

