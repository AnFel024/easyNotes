package com.example.easynotes.dto;

import com.example.easynotes.enumerator.RevisionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusNoteDto {

    private Long idNote;
    private Long idUser;
    // Aprobado // desaprobado
    private RevisionStatus status;
}
