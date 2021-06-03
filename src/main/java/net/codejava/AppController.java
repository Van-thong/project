package net.codejava;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class AppController {

    Logger logger = LoggerFactory.getLogger(AppController.class);

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private NoteRepository noteRepo;

	@GetMapping("")
	public String viewHomePage(Model model) {
		Long userId = getCurrentUserId();
		model.addAttribute("userId", userId);
		return "index";
	}
	
	@GetMapping("/home")
	public String viewHomePage2(Model model) {
		Long userId = getCurrentUserId();
		model.addAttribute("userId", userId);
		return "index2";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepo.save(user);
		
		return "register_success";
	}
	
	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> listUsers = userRepo.findAll();
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}
	
	@GetMapping("/notes")
	public String listNotes(Model model, @RequestParam(required=false,name="search") String search, @RequestParam(required=false,name="user") Long user) {
		if(search == null) {
			search = "";
		}
		
		if(user == null) {
			List<Note> listNotes = noteRepo.filterNotes(search);
			model.addAttribute("listNotes", listNotes);
		} else {
			List<Note> listNotes = noteRepo.filterNotes(search, user);
			model.addAttribute("listNotes", listNotes);
		}
		
		List<User> listUsers = userRepo.findAll();
		model.addAttribute("listUsers", listUsers);
		
		return "notes";
	}
	
	@GetMapping("/notes/create")
	public String showCreateNoteForm(Model model) {
		model.addAttribute("note", new Note());
		
		return "create_notes";
	}

	private Long getCurrentUserId() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId = null;
		if (principal instanceof CustomUserDetails) {
			userId = ((CustomUserDetails)principal).getUserId();
		} 
		
		return userId;
	}
	
	@PostMapping("/process_note")
	public String processNote(Note note) {
		note.setCreatedAt(LocalDateTime.now());
		Long userId = getCurrentUserId();
		if (userId != null) {
			note.setUserId(userId);
		}
		noteRepo.save(note);

		return "create_success";
	}
}
 