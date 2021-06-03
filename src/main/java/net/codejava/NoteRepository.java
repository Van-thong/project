package net.codejava;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("SELECT n FROM Note n JOIN User u ON u.id = n.userId WHERE n.title LIKE %?1% AND u.id=?2")
	public  List<Note> filterNotes(String search, Long user);
	
	@Query("SELECT n FROM Note n JOIN User u ON u.id = n.userId WHERE n.title LIKE %?1%")
	public  List<Note> filterNotes(String search);
}
