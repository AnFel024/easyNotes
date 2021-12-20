package com.example.easynotes.service;

import com.example.easynotes.dto.NoteRequestDTO;
import com.example.easynotes.dto.TypeNoteDTO;
import com.example.easynotes.enumerator.TypeNote;
import com.example.easynotes.exception.ResourceNotFoundException;
import com.example.easynotes.model.Note;
import com.example.easynotes.model.Thank;
import com.example.easynotes.model.User;
import com.example.easynotes.repository.NoteRepository;
import com.example.easynotes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import javax.naming.spi.ResolveResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class NoteServiceTest {
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    NoteRepository noteRepository = Mockito.mock(NoteRepository.class);

    ModelMapper modelMapper = new ModelMapper();

    NoteService noteService = new NoteService(noteRepository, userRepository, modelMapper);


    @Test
    void getAllNotes() {
        noteService.getAllNotes();
    }

    @Test
    void createNote() {
        when(noteRepository.save(any(Note.class))).thenReturn(new Note());
        Assertions.assertDoesNotThrow(
                () -> noteService.createNote(new NoteRequestDTO()) );
    }

    @Test
    void getNoteById() {
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.getNoteById(1L) );
    }

    @Test
    void updateNote() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> noteService.updateNote(1L, new Note()));
    }

    @Test
    void updateNoteOk() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(new Note()));
        when(noteRepository.save(any(Note.class))).thenReturn(new Note());
        Assertions.assertDoesNotThrow(
                () -> noteService.updateNote(1L, new Note()));
    }

    @Test
    void deleteNote() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> noteService.deleteNote(1L) );
    }

    @Test
    void addReviser() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> noteService.addReviser(2L, 1L) );
    }


    @Test
    void addReviserOk() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(noteRepository.findById(2L)).thenReturn(Optional.of(new Note()));
        Assertions.assertDoesNotThrow(
                () -> noteService.addReviser(2L, 1L) );
    }

    @Test
    void getThanks() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> noteService.getThanks(3L) );
    }

    @Test
    void getThanksOk() {
        var pedro = new User();
        var note = new Note();
        note.setThanks(Set.of(new Thank(pedro, note)));
        when(noteRepository.findById(3L)).thenReturn(Optional.of(note));
        Assertions.assertDoesNotThrow(
                () -> noteService.getThanks(3L) );
    }


    @Test
    void getThreeMoreThankedNotes() {
        noteService.getThreeMoreThankedNotes(2020);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,3,4}) //Menor que 5 deberia devolver Normal
    void getClasificationNoteNormal(int input){
        Long idNote = 1L;
        //Arrange (Cree el Note y User con los Setts porque son entidades
        // que sino requieren la carga de todos sus atributos
        Note note = new Note();
        User user = new User();
        user.setFirstName("J K Rowling");
        note.setAuthor(user);
        note.setTitle("Harry Potter");

        Map<String,Integer>numberOfThanks= Map.of("Count_thanks", input);

        //Arrange Expect
        TypeNoteDTO expectNote = new TypeNoteDTO(note.getTitle(),user.getFirstName(),TypeNote.NORMAL);

        Mockito.when(noteRepository.findById(idNote)).thenReturn(Optional.of(note));
        Mockito.when(noteRepository.findNumberOfThanksByNote(idNote)).thenReturn(numberOfThanks);

        //Assert
        TypeNoteDTO response = noteService.noteClasification(idNote);

        Mockito.verify(noteRepository,Mockito.atLeastOnce())
                .findById(idNote);
        Mockito.verify(noteRepository,Mockito.times(1))
                .findNumberOfThanksByNote(idNote);

        Assertions.assertEquals(expectNote,response);
    }

    @ParameterizedTest
    @ValueSource(ints = {5,7,9}) // DeInteres: Con 5 Thanks o más
    void getClasificationDeInteres(int input){
        Long idNote = 1L;
        //Arrange (Cree el Note y User con los Setts porque son entidades
        // que sino requieren la carga de todos sus atributos
        Note note = new Note();
        User user = new User();
        user.setFirstName("J K Rowling");
        note.setAuthor(user);
        note.setTitle("Harry Potter");

        Map<String,Integer>numberOfThanks= Map.of("Count_thanks", input);

        //Arrange Expect
        TypeNoteDTO expectNote = new TypeNoteDTO(note.getTitle(),user.getFirstName(),TypeNote.DE_INTERES);

        Mockito.when(noteRepository.findById(idNote)).thenReturn(Optional.of(note));
        Mockito.when(noteRepository.findNumberOfThanksByNote(idNote)).thenReturn(numberOfThanks);

        //Assert
        TypeNoteDTO response = noteService.noteClasification(idNote);

        Mockito.verify(noteRepository,Mockito.atLeastOnce())
                .findById(idNote);
        Mockito.verify(noteRepository,Mockito.times(1))
                .findNumberOfThanksByNote(idNote);

        Assertions.assertEquals(expectNote,response);
    }

    @ParameterizedTest
    @ValueSource(ints = {11,15,200}) // Con mas de 10 Thanks Destacada
    void getClasificationDestacada(int input){
        Long idNote = 1L;
        //Arrange (Cree el Note y User con los Setts porque son entidades
        // que sino requieren la carga de todos sus atributos
        Note note = new Note();
        User user = new User();
        user.setFirstName("J K Rowling");
        note.setAuthor(user);
        note.setTitle("Harry Potter");

        Map<String,Integer>numberOfThanks= Map.of("Count_thanks", input);

        //Arrange Expect
        TypeNoteDTO expectNote = new TypeNoteDTO(note.getTitle(),user.getFirstName(),TypeNote.DESTACADA);

        Mockito.when(noteRepository.findById(idNote)).thenReturn(Optional.of(note));
        Mockito.when(noteRepository.findNumberOfThanksByNote(idNote)).thenReturn(numberOfThanks);

        //Assert
        TypeNoteDTO response = noteService.noteClasification(idNote);

        Mockito.verify(noteRepository,Mockito.atLeastOnce())
                .findById(idNote);
        Mockito.verify(noteRepository,Mockito.times(1))
                .findNumberOfThanksByNote(idNote);

        Assertions.assertEquals(expectNote,response);
    }
}