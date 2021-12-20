package com.example.easynotes.unit.service;

import com.example.easynotes.dto.UserCategoryResponseDTO;
import com.example.easynotes.enumerator.RevisionStatus;
import com.example.easynotes.model.Note;
import com.example.easynotes.model.User;
import com.example.easynotes.repository.NoteRepository;
import com.example.easynotes.repository.UserRepository;
import com.example.easynotes.service.NoteService;
import com.example.easynotes.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;



    @Test
    public void testShouldBeClasifyDairy(){
        List<User> users = List.of(
                new User(1l,
                        Set.of(
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now(),LocalDate.now(), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now().minusDays(1),LocalDate.now(), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now().minusDays(2),LocalDate.now(), RevisionStatus.PENDIENTE)

                        ),
                "","",Set.of(),Set.of())

        );
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserCategoryResponseDTO> dtos = userService.getAllUsersCategory();
        assertEquals(dtos.size(),1);
        assertEquals(dtos.get(0).getCategory(),"PublicadorDiario");

    }


    @Test
    public void testShouldBeClasifyWeekly(){
        List<User> users = List.of(
                new User(1l,
                        Set.of(
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now(),LocalDate.now(), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now().minusWeeks(1),LocalDate.now(), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now().minusWeeks(2),LocalDate.now(), RevisionStatus.PENDIENTE)

                        ),
                        "","",Set.of(),Set.of())

        );
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserCategoryResponseDTO> dtos = userService.getAllUsersCategory();
        assertEquals(dtos.size(),1);
        assertEquals(dtos.get(0).getCategory(),"PublicadorSemanal");

    }


    @Test
    public void testShouldBeClasifyPubliser(){
        List<User> users = List.of(
                new User(1l,
                        Set.of(
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now(),LocalDate.now(), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now(),LocalDate.now().minusDays(1), RevisionStatus.PENDIENTE),
                                new Note(1l,new User()," "," ", Set.of(),Set.of(), LocalDate.now(),LocalDate.now().minusDays(1), RevisionStatus.PENDIENTE)

                        ),
                        "","",Set.of(),Set.of())

        );
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserCategoryResponseDTO> dtos = userService.getAllUsersCategory();
        assertEquals(dtos.size(),1);
        assertEquals(dtos.get(0).getCategory(),"Publicador");

    }


}
