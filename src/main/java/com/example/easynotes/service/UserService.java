package com.example.easynotes.service;

import com.example.easynotes.dto.*;
import com.example.easynotes.exception.ResourceNotFoundException;
import com.example.easynotes.model.Note;
import com.example.easynotes.model.Thank;
import com.example.easynotes.model.User;
import com.example.easynotes.repository.NoteRepository;
import com.example.easynotes.repository.ThankRepository;
import com.example.easynotes.repository.UserRepository;
import com.example.easynotes.utils.ListMapper;
import org.apache.tomcat.jni.Local;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService implements IUserService {

    UserRepository userRepository;

    NoteRepository noteRepository;

    ThankRepository thankRepository;

    ModelMapper modelMapper;

    ListMapper listMapper;

    @PersistenceContext
    EntityManager entityManager;

    public UserService(UserRepository userRepository,
                NoteRepository noteRepository,
                ThankRepository thankRepository,
                ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.thankRepository = thankRepository;

        modelMapper=new ModelMapper();

        Converter<Long, User> authorIdToUserConverter = new AbstractConverter<Long, User>() {
            @Override
            protected User convert(Long authorId) throws ResourceNotFoundException {
                return userRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
            }
        };
        //Load converter to modelMapper used when we want convert from User to UserResponseWithCantNotesDTO
        modelMapper.typeMap(NoteRequestDTO.class, Note.class).addMappings( (mapper) ->
                mapper.using(authorIdToUserConverter)
                        .map(NoteRequestDTO::getAuthorId, Note::setAuthor)
        );

        this.modelMapper = modelMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> listUsers = userRepository.findAll();
        return listMapper.mapList(listUsers, UserResponseDTO.class);
    }

    @Override
    public List<UserResponseWithNotesDTO> getAllUsersWithNotes() {
        List<User> listUsers = userRepository.findAll();
        return listMapper.mapList(listUsers, UserResponseWithNotesDTO.class);
    }

    @Override
    public List<UserResponseWithCantNotesDTO> getAllUsersWithCantNotes() {
        List<User> listUsers = userRepository.findAll();
        return listMapper.mapList(listUsers, UserResponseWithCantNotesDTO.class);
    }

    @Override
    public UserResponseDTO createUSer(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseWithNotesDTO getUserWithNotesById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return modelMapper.map(user, UserResponseWithNotesDTO.class);
    }
    @Override
    public UserResponseWithCantNotesDTO getUserWithCantNotesById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return modelMapper.map(user, UserResponseWithCantNotesDTO.class);
    }

    @Override
    public List<UserCategoryResponseDTO> getAllUsersCategory() {
        List<User> userList= userRepository.findAll();

        return userList.stream()
                .map(user -> new UserCategoryResponseDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        publicationType(
                                user.getAuthorNotes()
                                        .stream()
                                        .map(Note::getCreatedAt)
                                        .collect(Collectors.toList())
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * Con esta configuración se toma el primer día de la semana. (p.e. para el 20/12 tomaría el domingo 19/12)
     * Lo usa en la capa del servicio, trayendo todas las notas e iterando sobre ellas.
     * @param datesList
     * @return tipo de publicador
     */
    private String publicationType(List<LocalDate> datesList){
        System.out.println("Prueba");
        datesList.forEach(System.out::println);
        int daysFlag= 0;
        int weekFlag= 0;

        for (int i = 0; i <3; i++) {
            final int pos= i;
            TemporalField field= WeekFields.of(Locale.US).dayOfWeek();
            if (datesList.stream()
                    .anyMatch(date -> LocalDate.now().minusDays(pos).compareTo(date) == 0))
                daysFlag++;

            // date.minus(n+1)<d < date.minus(n)

            // date.minus(n+1)>d && date.minus(n)<d
            if (datesList.stream()
                    .anyMatch(date -> (
                            date.isAfter(LocalDate.now()
                                .minusWeeks(pos).with(field,1)) &
                            date.isBefore(LocalDate.now()
                                .minusWeeks(pos).with(field,7)) |
                                    (date.isEqual(LocalDate.now()
                                        .minusWeeks(pos).with(field,1)) |
                                    date.isEqual(LocalDate.now()
                                        .minusWeeks(pos).with(field,7)))
                    )))
                    //.anyMatch(date -> LocalDate.now().minusDays((pos+1)*7).compareTo(date.minusDays(pos*7)) < 0))
                weekFlag++;

        }

        if (daysFlag==3)
            return "PublicadorDiario";
        else if (weekFlag==3)
            return "PublicadorSemanal";
        else
            return "Publicador";
    }

    /**
     * Endpoint Get con la funcionalidad de traer un CategoriaUsuario
     *     PublicadorDiario :En cada una de los tres días hayan publicado al menos una nota en cada dia
     *     PublicadorSemanal : En cada una de las tres semanas ha publicado al menos una nota en cada semana
     *     Publicador: No cumple las anteriores
     */

    @Override
    public UserResponseDTO updateUser(Long userId,
                                      UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.delete(user);

    }

    @Override
    public List<UserResponseDTO> getUsersLastNameLike(String lastName) {
        List<?> users =  userRepository.findUserByLastNameLike(lastName);

        return null;
    }

    @Override
    public List<UserResponseWithNotesDTO> getUsersByNoteTitleLike(String title) {
        List<User> users = userRepository.findUserByNoteTitleLike(title);

        return listMapper.mapList(users, UserResponseWithNotesDTO.class);
    }

    @Override
    public List<UserResponseWithNotesDTO> getUsersByNoteCreatedAfterDate(Date date) {
        List<User> users = userRepository.findUserByNoteCreatedAtLessOrEqualDate(date);

        return listMapper.mapList(users, UserResponseWithNotesDTO.class);
    }

    @Override
    public void createThank(Long userId, Long noteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        Thank thank = new Thank(user, note);

        thankRepository.save(thank);
    }

//    @Override
//    public List<UserResponseDTO> getUsersLastNameLikeAndFirstNameLike(String lastName, String firstName) {
//        List<User> users = userRepository.findUserByLastNameLikeAndFirstNameContains(lastName, firstName);
//
//        return listMapper.mapList(users, UserResponseDTO.class);
//    }


    // NOTA: MALA PRACTICA 1 (entityManager)
    @Override
    public UserResponseDTO getUserById(Integer id) {
        User user = (User) entityManager.createQuery("from user where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        return modelMapper.map(user, UserResponseDTO.class);
    }


    // NOTA: MALA PRACTICA 2 (@NameQuery y entityManager)
    @Override
    public UserResponseDTO getUserByLastName(String lastName) {
        TypedQuery<User> query = entityManager.createNamedQuery("getUserByLastName", User.class);
        query.setParameter("lastName", lastName);

        User user = query.getResultList().get(1);
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
